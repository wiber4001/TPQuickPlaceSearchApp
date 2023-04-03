package com.wny2023.tpquickplacesearchapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object{ //java의 static method에 해당
        fun getRetrofitInstance(baseUrl:String):Retrofit {
            val retrofit:Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
    }
}