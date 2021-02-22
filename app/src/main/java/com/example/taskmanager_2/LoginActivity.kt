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

        title = "Login"

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener() {
            viewModel = MainViewModel()

            var loginUser = LoginUser()
            loginUser.name = editTextLoginName.text.toString().trim()
            loginUser.password = editTextLoginPassword.text.toString().trim()

            viewModel.doLogin(loginUser, token).observe(this, Observer { networkResource ->
                when (networkResource.status) {
                    Status.LOADING -> {
                        Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        val message = networkResource.data
                        message?.let {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)

                            val editor = token.edit()
                            editor.putString("isLoggedIn", loginUser.name)
                            editor.commit()

                            startActivity(intent)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}