package com.wny2023.tpquickplacesearchapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.wny2023.tpquickplacesearchapp.G
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityLoginBinding
import com.wny2023.tpquickplacesearchapp.model.UserAccount

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

    }
    private fun clickedLoginGoogle(){
        // Google에서 검색 [안드로이드 구글 로그인]

    }
    private fun clickedLoginNaver(){

    }


}