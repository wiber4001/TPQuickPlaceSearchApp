package com.wny2023.tpquickplacesearchapp.network

import com.wny2023.tpquickplacesearchapp.model.KakaoSearchPlaceResponse
import com.wny2023.tpquickplacesearchapp.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitAPIService {

    //네아로 사용자 정보 API
    @GET("/v1/nid/me") //path 써줌
    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>

    //카카오 키워드 장소 검색 API (에 대한 요청작업, 장소검색)
    @Headers("Authorization: KakaoAK 0adbe9ca5b52bc5ba64a6123b0901d16")
    // 앱 REST API키를 헤더에 담에 GET으로 요청(사이트 지시사항)
    @GET("/v2/local/search/keyword.json") //path 써줌
    fun searchPlace(@Query("query")query:String, @Query("y")latitude:String, @Query("x")longitude:String): Call<KakaoSearchPlaceResponse>

}