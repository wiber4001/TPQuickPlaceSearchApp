package com.wny2023.tpquickplacesearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding:ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //기본적인 웹뷰의 필수 3종 세팅
        //webViewClient를 만들어 주지않으면 기본 크롬브라우저를 열어버림
        binding.webView.webViewClient= WebViewClient() // 현재 웹뷰 안에서 웹문서 열리도록
        //웹문서 안에서 다이알로그같은 것들을 발동하도록 허용해주는 설정
        binding.webView.webChromeClient= WebChromeClient()
        //settings라는 객체로 웹뷰의 설정을 컨트롤 함
        //웹문서가 작동할수 있으려면 아래를 설정해야함
        binding.webView.settings.javaScriptEnabled =true //default로 는 false로 자바스크립트 비허용

        var place_url:String= intent.getStringExtra("place_url") ?: "" //null대신 빈문자
        binding.webView.loadUrl(place_url) //여기는 nullable이 안됨

    }

    override fun onBackPressed() {
        if(binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()

    }
}