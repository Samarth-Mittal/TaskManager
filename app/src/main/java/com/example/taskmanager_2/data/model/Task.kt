package com.example.taskmanager_2.data.model


import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("assigne_id")
    val assigneId: Member,
    @SerializedName("description")
    val description: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("planneddate")
    val planneddate: Any,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("reporter_id")
    val reporterId: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String
)