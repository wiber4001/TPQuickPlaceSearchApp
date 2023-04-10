package com.wny2023.tpquickplacesearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.wny2023.tpquickplacesearchapp.G
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityLoginBinding
import com.wny2023.tpquickplacesearchapp.model.NidUserInfoResponse
import com.wny2023.tpquickplacesearchapp.model.UserAccount
import com.wny2023.tpquickplacesearchapp.network.RetrofitAPIService
import com.wny2023.tpquickplacesearchapp.network.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 버튼 클릭으로 로그인없이 Main화면으로 이동
        binding.tvLogo.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        //이메일로 회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener{
            //회원가입 화면으로 전환
            startActivity(Intent(this,SignupActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener{
            //이메일 로그인 화면으로 전환
            startActivity(Intent(this,EmailSigninActivity::class.java))
        }

        //간편로그인 버튼들 클릭
        binding.ivLoginKakao.setOnClickListener{ clickedLoginKakao()}
        binding.ivLoginGoogle.setOnClickListener{ clickedLoginGoogle()}
        binding.ivLoginNaver.setOnClickListener{ clickedLoginNaver()}

        //kakao 키해시 값 얻어오기
        val keyHash:String=Utility.getKeyHash(this)
        Log.i("keyHash",keyHash)

    } //onCreate()..

    private fun clickedLoginKakao(){
        
        //카카오 로그인의 공통 callback함수를 만듬
        val callback:(OAuthToken?,Throwable?)->Unit = {token,error->
            if(token!=null){
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
                //사용자의 정보 요청 [1.회원번호, 2.이메일주소]
                UserApiClient.instance.me { user, error ->
                    if(user!=null){
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email ?:"" //혹시 null이면 email의 기본값은 "" (elvis연산자 사용)

                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id,email)

                        //main화면으로 이동
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }
                
            }else{
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
            
        }

        //카카오톡이 설치되어 있으면 카톡으로 로그인, 아니면 카카오계정으로 로그인 -권장사항 
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this,callback = callback )
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this,callback = callback )
        }

    } //카카오 로그인


    private fun clickedLoginGoogle(){
        // Google에서 검색 ['android google login' 으로 영어검색]

        // 구글 로그인 옵션객체(동의항목에 해당) 생성 - Builder 이용
        val signInOptions:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        // 디폴트구글계정(not게임계정) 으로 로긴하고 email만 요청

        // 구글 로그인 화면(액티비티)을 실행하는 Intent를 통해 로그인을 구현 - Intent형 객체를 이용함
        val intent:Intent= GoogleSignIn.getClient(this,signInOptions).signInIntent
        resultLauncher.launch(intent)
    }// 구글로그인


    // 구글 로그인 화면(액티비티)의 실행결과를 받아오는 계약체결 대행사
    val resultLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object :ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            //로그인 결과를 가져온 인텐트(택배기사) 객체 소환
            val intent:Intent?= result?.data

            //돌아온 Intent로부터 구글 계정 정보를 가져오는 작업 수행
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account:GoogleSignInAccount = task.result
            var id:String= account.id.toString()
            var email:String = account.email ?:""

            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
            G.userAccount= UserAccount(id,email)

            //main화면으로 이동
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
            finish()
        }
    }) //구글로그인 계약체결 멤버변수

    private fun clickedLoginNaver(){
        // 네아로 초기화
        NaverIdLoginSDK.initialize(this,"YQIVLX_PTMjxDlxTmNyK","DP5ZW1Mi3Q","어디야")
        // 네이버 로그인
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "server error:$message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "로그인실패:$message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                //사용자 정보를 가져오는 REST API 를 작업할때 접속토큰이 필요함
                val accessToken:String? = NaverIdLoginSDK.getAccessToken()
                //토큰값 확인
                Log.i("token",accessToken.toString())

                //레트로핏으로 사용자 정보 API를 가져오기
                val retrofit = RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                retrofit.create(RetrofitAPIService::class.java).getNidUserInfo("Bearer $accessToken").enqueue(object:Callback<NidUserInfoResponse>{
                    override fun onResponse(
                        call: Call<NidUserInfoResponse>,
                        response: Response<NidUserInfoResponse>
                    ) {
                        val userInfoResponse : NidUserInfoResponse?= response.body()
                        val id:String = userInfoResponse?.response?.id ?:"" //elvis 식으로 null처리
                        val email:String = userInfoResponse?.response?.email ?:""
                        Toast.makeText(this@LoginActivity, "$email 로 로그인", Toast.LENGTH_SHORT).show()
                        G.userAccount = UserAccount(id,email)

                        // main화면으로 이동
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "회원정보 불러오기 실패:${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })


            }

        })

    }//naver 로그인


}