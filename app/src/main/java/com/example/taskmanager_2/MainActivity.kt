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

        btnLogin_MainActivity.setOnClickListener(){
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignUp_MainActivity.setOnClickListener(){
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}