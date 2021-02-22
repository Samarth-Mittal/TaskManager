package com.example.taskmanager_2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Task Manager"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        if(!token.getString("isLoggedIn", "").equals("")){
            btn_MainActivity.text = "Tap to open Dashboard"
            btn_MainActivity.setOnClickListener(){
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }else {
            btn_MainActivity.text = "Tap to login"
            val editor = token.edit()
            editor.putInt("isFirstLogin", 0)
            editor.commit()
            btn_MainActivity.setOnClickListener() {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}