package com.example.taskmanager_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.SignUpUser
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private var viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp.setOnClickListener() {
            viewModel = MainViewModel()

            var signUpUser = SignUpUser()
            signUpUser.name = editTextSignUpName.text.toString().trim()
            signUpUser.email = editTextSignUpEmail.text.toString().trim()
            signUpUser.password = editTextSignUpPassword.text.toString().trim()
            var confirm_password = editTextSignUpConfirmPassword.text.toString().trim()
            signUpUser.invite_id = editTextSignUpInviteID.text.toString().trim()
            signUpUser.phone = editTextSignUpPhone.text.toString().trim()

            //validate fields for null and incorrect

            //password and confirm_password similarity check
            if (signUpUser.password.equals(confirm_password)) {

                viewModel.doRegistration(signUpUser).observe(this, Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                            Toast.makeText(this, "loading data from network", Toast.LENGTH_SHORT)
                                .show()
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "error loading data from network",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}