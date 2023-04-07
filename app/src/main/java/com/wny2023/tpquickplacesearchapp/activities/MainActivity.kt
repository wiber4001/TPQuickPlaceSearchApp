package com.wny2023.tpquickplacesearchapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.ListFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.protobuf.DescriptorProtos
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityMainBinding
import com.wny2023.tpquickplacesearchapp.fragments.PlaceListFragment
import com.wny2023.tpquickplacesearchapp.fragments.PlaceMapFragment
import com.wny2023.tpquickplacesearchapp.model.KakaoSearchPlaceResponse
import com.wny2023.tpquickplacesearchapp.model.Place
import com.wny2023.tpquickplacesearchapp.model.PlaceMeta
import com.wny2023.tpquickplacesearchapp.network.RetrofitAPIService
import com.wny2023.tpquickplacesearchapp.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //카카오 맵 검색에 필요한 요청 데이터: query(검색장소명), x(경도:longitude),y(위도:latitude)
    // 1.검색 장소명
    var searchQuery:String="화장실" //앱의 초기검색어 - 내 주변 개방화장실
    // 2. 현재 내 위치 정보 객체(위도와 경도 정보를 멤버로 보유한 객체
    var myLocation:Location?=null //사용자가 gps를 허용하지 않아서 없을 수도-> 자동으로 서울시청
    // [Google Fused Location API 사용: play-services-location]
    val providerClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    // 카카오 장소 키워드 검색결과 응답객체 참조변수
    var searchPlaceResponse:KakaoSearchPlaceResponse?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //작업
        // toolbar를 제목줄로 대체 - 옵션메뉴를 가지도록
        setSupportActionBar(binding.toolbar)

        //첫번째 보여질 프레그먼트 동적 추가
        supportFragmentManager.beginTransaction().add(R.id.container_fragment,PlaceListFragment()).commit()

        //탭 레이아웃의 탭버튼 클릭시에 보여줄 프레그먼트 변경
        binding.tabLayout.addOnTabSelectedListener(object :OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                 if(tab?.text=="LIST"){
                     supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()
                 }else if(tab?.text=="MAP"){
                     supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceMapFragment()).commit()
                 }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        //검색기능을 만들자-소프트키보드의 검색버튼을 클릭하였을때...
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            searchQuery=binding.etSearch.text.toString()
            //카카오 검색API를 이용하여 장소들 검색하기
            searchPlace()
            false
        }

        //특정 키워드 단축 검색 버튼들에 리스너를 처리하는 함수를 호출
        setChoiceButtonsListener()

        //내 위치 정보 제공에 대한 동적 퍼미션 요청
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED ) {
            //퍼미션 요청 대행사 부르기
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //내 위치 요청
            requestMyLocation()
        }
        //fine과coarse는 그룹이라 둘중 1개만만드면됨

    }//onCreate()..

    //퍼미션 요청 대행사 계약 및 등록
    val permissionLauncher:ActivityResultLauncher<String> =registerForActivityResult(ActivityResultContracts.RequestPermission(),object:ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {
            if (result!!) requestMyLocation()
            else Toast.makeText(this@MainActivity, "검색기능수행불가, 허용필요", Toast.LENGTH_SHORT).show()
        }
    })

    //내 위치 요청 작업 메소드
    private fun requestMyLocation(){
        //위치검색 기준 설정하는 요청 객체
        val request:LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        // 높은정확도, 1초마다 갱신
        //실시간 위치정보 갱신 요청 - permission 체크가 필요...
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

    }

    //위치검색 결과 콜백객체
    private val locationCallback: LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation=p0.lastLocation
            //위치 탐색되었으니 실시간 업데이트를 종료
            providerClient.removeLocationUpdates(this) //this는 locationCallBack객체
            // 위치 정보 얻었으니 정보 검색시작
            searchPlace()

        }
    }

    //특정 키워드 단축 검색 버튼들의 리스너 처리
    private fun setChoiceButtonsListener(){
        binding.layoutChoice.choiceWc.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choiceMovie.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choiceEv.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choiceCoffee.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choicePark.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choicePharm.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choiceFood.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choice1.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choice2.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choice3.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choice4.setOnClickListener{clickChoice(it)}
        binding.layoutChoice.choice5.setOnClickListener{clickChoice(it)}
    }

    //멤버변수 영역
    var choiceID=R.id.choice_wc

    private fun clickChoice(view: View){
        //기존 선택되었던 버튼을 찾아 배경이지미를 흰색 원 그림으로 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)
        //현재 클릭된 버튼의 배경을 회색원으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)
        //다음 클릭시에 이전 클릭된 뷰의 ID를 기억하도록
        choiceID=view.id

        //초이스 한 것에 따라 검색장소명을 변경하여 다시 검색..
        when(choiceID){
            R.id.choice_wc->searchQuery="화장실"
            R.id.choice_food->searchQuery="패스트푸드"
            R.id.choice_movie->searchQuery="영화관"
            R.id.choice_ev->searchQuery="충전소"
            R.id.choice_park->searchQuery="공원"
            R.id.choice_pharm->searchQuery="약국"
            R.id.choice_coffee->searchQuery="카페"
            R.id.choice_1->searchQuery="버스정류장"
            R.id.choice_2->searchQuery="맛집"
            R.id.choice_3->searchQuery="버스정류장"
            R.id.choice_4->searchQuery="맛집"
            R.id.choice_5->searchQuery="버스정류장"
        }
        //새로운 검색시작
        searchPlace()
        //검색창에 글씨가 있다면 지우기
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus() //포커스도 뺏기
    }

    //카카오 장소 검색 API를 파싱하는 작업메소드
    private fun searchPlace(){
//        Toast.makeText(this, "$searchQuery-${myLocation?.latitude},${myLocation?.longitude}", Toast.LENGTH_SHORT).show()
        //kakao keyword place search api...REST API 작업- Retrofit
        val retrofit:Retrofit=RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com") //Host 를 써줌
        val retrofitAPIService= retrofit.create(RetrofitAPIService::class.java)
        retrofitAPIService.searchPlace(searchQuery,myLocation?.latitude.toString(),myLocation?.longitude.toString()).enqueue(object:Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse= response.body()
//                Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()
                //먼저 데이터가 온전히 잘 왔는지 파악하기 위해...
                val meta: PlaceMeta?= searchPlaceResponse?.meta
                val documents:MutableList<Place>? = searchPlaceResponse?.documents
                AlertDialog.Builder(this@MainActivity).setMessage("${meta?.total_count} \n ${documents?.get(0)?.place_name}").show()

                //무조건 검색이 완료되면 ListFragment부터 보여주기
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment,PlaceListFragment()).commit()

                //탭버튼의 위치를 ListFragment tab으로 변경
                binding.tabLayout.getTabAt(0)?.select()

            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_aa-> Toast.makeText(this, "검색장소를 입력/터치하면 찾는 앱 입니다.", Toast.LENGTH_SHORT).show()
            R.id.menu_bb-> Toast.makeText(this, "Retrofit2, Glide, Kakao API, Google API, Naver API, GSON", Toast.LENGTH_SHORT).show()

        }
        return super.onOptionsItemSelected(item)
    }

}