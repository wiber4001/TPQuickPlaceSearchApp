package com.wny2023.tpquickplacesearchapp.network

import com.wny2023.tpquickplacesearchapp.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitAPIService {

    //네아로 사용자 정보 API
    @GET("/v1/nid/me") //path 써줌
    fun getNidUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>

}