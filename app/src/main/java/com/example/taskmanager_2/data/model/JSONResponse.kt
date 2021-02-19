package com.example.taskmanager_2.data.model

import com.google.gson.annotations.SerializedName

data class JSONResponse (
    @SerializedName("message")
    val response_string: String
)