package com.wny2023.tpquickplacesearchapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.wny2023.tpquickplacesearchapp.G
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityEmailSigninBinding
import com.wny2023.tpquickplacesearchapp.model.UserAccount

class EmailSigninActivity : AppCompatActivity() {

    lateinit var binding:ActivityEmailSigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toobar)
        //툴바에 업버튼 보이기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.loginBtn.setOnClickListener { clickSignIn() }

    }//onCreate()..

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignIn(){

        var email:String=binding.etEmailaddress.text.toString()
        var password:String=binding.etPassword.text.toString()

        //Firebase Firestore DB에서 이메일과 패스워드 확인
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .whereEqualTo("password",password)
            .get().addOnSuccessListener {
                if(it.documents.size>0){
                    //로그인이 성공..
                    var id:String = it.documents[0].id //document 이름 (임의로 부여된 식별가능-중복없음-필드값)
                    G.userAccount= UserAccount(id,email)

                    //로그인 성공되었으니 곧바로 MainActivity로 이동..
                    val intent:Intent =Intent (this,MainActivity::class.java)
                    //기존 task의 모든 액티비티를 제거하고 새로운 task로 시작.
                    //[why? EmailSignInActivity 뿐만 아니라 LoginActivity 도
                    // back stack에 존재하기에 한꺼번에 finish 하기위해 back stack을 지우고 새로 시작]
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }else{
                    //로그인 성공이 안되었다면...
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주시기바랍니다.").create().show()
                    binding.etEmailaddress.requestFocus()
                    binding.etEmailaddress.selectAll()
                    binding.etPassword.text.clear()
                }
            }.addOnFailureListener() {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}