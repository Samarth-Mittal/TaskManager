package com.example.taskmanager_2.data.model


import com.google.gson.annotations.SerializedName

data class TeamsItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)