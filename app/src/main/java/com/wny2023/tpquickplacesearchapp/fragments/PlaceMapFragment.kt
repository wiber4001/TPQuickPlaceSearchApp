package com.wny2023.tpquickplacesearchapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wny2023.tpquickplacesearchapp.activities.MainActivity
import com.wny2023.tpquickplacesearchapp.activities.PlaceUrlActivity
import com.wny2023.tpquickplacesearchapp.databinding.FragmentPlaceMapBinding
import com.wny2023.tpquickplacesearchapp.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener

class PlaceMapFragment : Fragment() {

    lateinit var binding: FragmentPlaceMapBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPlaceMapBinding.inflate(inflater,container,false)
        return binding.root
    }

    val mapView:MapView by lazy { MapView(context) } //맵뷰객체 생성

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.containerMapview.addView(mapView)

        //마커나 말풍선의 클릭 이벤트에 반응하는 리스너 등록 - 반드시 마커 추가(지도설정)보다 먼저 등록
        mapView.setPOIItemEventListener(markerEventListener)

        //지도관련 설정 (지도위치, 마커추가, 등등)
        setMapAndMarkers()
    }
    private fun setMapAndMarkers(){
        // 맵의 중심좌표를 내가 원하는 위치로 변경
        // 현재 내 위치 위도, 경도 좌표 (nullable이므로 서울시청 위도경도를 임의로 default 설정)
        var lat:Double = (activity as MainActivity).myLocation?.latitude ?:  126.9779
        var lng:Double = (activity as MainActivity).myLocation?.longitude ?: 37.5663

        var myMapPoint:MapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)
        mapView.setMapCenterPointAndZoomLevel(myMapPoint,3,true)
        mapView.zoomIn(true)
        mapView.zoomOut(true)

        // 내 위치를 표시하는 마커 추가
        var marker= MapPOIItem()
        marker.apply {
            itemName="ME"
            mapPoint=myMapPoint
            markerType=MapPOIItem.MarkerType.YellowPin
            selectedMarkerType=MapPOIItem.MarkerType.BluePin
        }
        mapView.addPOIItem(marker)

        // 검색장소들의 마커 추가
        val documents:MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
        documents?.forEach{
            val point:MapPoint=MapPoint.mapPointWithGeoCoord(it.y.toDouble(),it.x.toDouble())
            val marker= MapPOIItem().apply {
                itemName=it.place_name
                mapPoint=point
                markerType=MapPOIItem.MarkerType.RedPin
                selectedMarkerType=MapPOIItem.MarkerType.BluePin
                //마커객체에 보관하고 싶은 데이터가 있다면,
                //즉, 해당 마커에 관련된 정보를 가지고 있는 객체를 마커에 설정저장해두기
                userObject=it
            }
            mapView.addPOIItem(marker)
        }

    }
    //멤버변수) 마커 or 말풍선 클릭 이벤트 리스너
    val markerEventListener:POIItemEventListener = object : POIItemEventListener{
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            //마커를 클릭했을때 발동

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            //deprecated 되었음, 아래의 메소드로 대체
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            //말풍선 터치가 일어났을때 발동
            // p1: 클릭한 마커의 객체
//                if(p1?.userObject==null) return
            p1?.userObject ?:return
            val place:Place =p1?.userObject as Place
            val intent= Intent(context,PlaceUrlActivity::class.java)
            intent.putExtra("place_url",place.place_url)
            startActivity(intent)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그할때 발동

        }
    }
}