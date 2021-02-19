package com.example.taskmanager_2.data.model

import com.google.gson.annotations.SerializedName

class LoginResponse (
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val response_string: String
)