package com.wny2023.tpquickplacesearchapp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //kakao SDK 초기화- 개발자 사이트에 등록한 [네이티브 앱키]
        //나머지 설정파라미터들은 모두 디폴트 그대로 두고간다
        KakaoSdk.init(this,"b5bd3a2bf50bf6e787e388893bb33fc2")
    }
}