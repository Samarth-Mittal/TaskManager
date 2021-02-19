package com.example.taskmanager_2.data.model


import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String
)