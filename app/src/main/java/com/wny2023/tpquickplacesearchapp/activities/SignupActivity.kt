package com.wny2023.tpquickplacesearchapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var binding:ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toobar)
        //액션바에 업버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //
        binding.signupBtn.setOnClickListener { clickSignUp() }
    }//onCreate()

    //뒤로가기 누르면 뒤로가게 만들기(반응하기)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp(){
        // Firebase Firestore DB에 사용자 정보 저장하기
        var email:String=binding.etEmailaddress.text.toString()
        var password:String=binding.etPassword.text.toString()
        var passwordConfirm:String=binding.etPasswordConfirm.text.toString()

        // 유효성 검사 - 패스워드와 패스워드 확인이 맞는지 검사
        // 코틀린은 == 으로 .equls 대체가능
        if(password != passwordConfirm){
            AlertDialog.Builder(this).setMessage("패스워드가 같지 않습니다. 다시 입력해주시기 바랍니다.").create().show()
            binding.etPasswordConfirm.requestFocus()
            binding.etPasswordConfirm.selectAll()
            return
        }
        // 파이어스토어
        val db=FirebaseFirestore.getInstance()

        // 저장할 값(이메일, 비밀번호)을 HashMap으로 저장
        val user:MutableMap<String,String> = mutableMapOf()
        user.put("email",email)
        user["password"] = password

        //Collection 명은"emailUsers" 로 지정 [RDBMS의테이블명 같은 역할]
        //중복된 email을 가진 회원정보가 있을 수 있으니 확인..
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .get().addOnSuccessListener {

                //만약 같은 값을 가진 document가 있다면...사이즈가 0개 이상일 것이므로
                if(it.documents.size>0){
                    AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다. 다시 확인하여 주시기 바랍니다.").show()
                    binding.etEmailaddress.requestFocus()
                    binding.etEmailaddress.selectAll()
                }else{
                    //랜덤하게 만들어지는 document명을 회원 id값으로 사용할 예정
                    db.collection("emailUsers").add(user).addOnSuccessListener {
                        AlertDialog.Builder(this)
                            .setMessage("축하합니다.\n 회원가입이 완료되었습니다.")
                            .setPositiveButton("확인", object :OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    finish()
                                }
                            }).create().show()
                    }//저장하는 코드
                }
            }


    }
}