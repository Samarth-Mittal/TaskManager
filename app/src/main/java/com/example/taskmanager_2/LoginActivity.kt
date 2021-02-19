package com.example.taskmanager_2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.LoginUser
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener() {
            viewModel = MainViewModel()

            var loginUser = LoginUser()
            loginUser.name = editTextLoginName.text.toString().trim()
            loginUser.password = editTextLoginPassword.text.toString().trim()

            viewModel.doLogin(loginUser, token).observe(this, Observer { networkResource ->
                when (networkResource.status) {
                    Status.LOADING -> {
                        Toast.makeText(this, "loading data from network", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        val message = networkResource.data
                        message?.let {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)

                            //finish()

                            val editor = token.edit()
                            editor.putString("Name", loginUser.name)
                            editor.commit()

                            //note to self: add logic to send user info to HomeActivity
                            startActivity(intent)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "error loading data from network", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}