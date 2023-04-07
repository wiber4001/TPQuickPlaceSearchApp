package com.wny2023.tpquickplacesearchapp.model

data class KakaoSearchPlaceResponse(var meta:PlaceMeta, var documents:MutableList<Place>)

data class PlaceMeta(var total_count:Int, var pageable_count:Int, var is_end:Boolean)

data class Place(
    var id:String,
    var place_name:String,
    var category_name:String,
    var category_group_code:String,
    var category_group_name:String,
    var phone:String,
    var address_name:String,
    var road_address_name:String,
    var x:String, //longitude
    var y:String, //latitude
    var place_url:String,
    var distance:String //중심 좌표까지의 거리, 단, 요청 파라미터로 x,y좌표를 준 경우만 존재, 단위는 meter
    )