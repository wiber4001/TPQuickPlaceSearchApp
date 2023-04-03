package com.wny2023.tpquickplacesearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.wny2023.tpquickplacesearchapp.R
import com.wny2023.tpquickplacesearchapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //작업
        // toolbar를 제목줄로 대체 - 옵션메뉴를 가지도록
        setSupportActionBar(binding.toolbar)
    }//onCreate()..

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_aa-> Toast.makeText(this, "menu_aa", Toast.LENGTH_SHORT).show()
            R.id.menu_bb-> Toast.makeText(this, "menu_bb", Toast.LENGTH_SHORT).show()

        }
        return super.onOptionsItemSelected(item)
    }

}