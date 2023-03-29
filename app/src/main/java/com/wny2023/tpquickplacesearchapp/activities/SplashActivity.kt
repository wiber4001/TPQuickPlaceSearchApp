package com.wny2023.tpquickplacesearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.wny2023.tpquickplacesearchapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_splash)
        // 테마를 이용하여 화면구현->layout이 필요없음->삭제

        // 단순하게 1.5초 후에 로그인 화면(Activity)으로 전환
        // 메인스레드에게 sleep을 걸면 안됨!!
        // handler (요청을 메인스레드에게 전달하는 별도스레드)
//        Handler(Looper.getMainLooper()).postDelayed( object:Runnable{
//            override fun run() {
//
//            }
//        },1500) //버전업 된 핸들러-루퍼를 명시적으로 써줘야함
        //람다식으로 축약가능
        Handler(Looper.getMainLooper()).postDelayed({
           startActivity(Intent(this, LoginActivity::class.java))
           finish()
        },1500)

    }
}