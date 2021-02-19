package com.example.taskmanager_2.data.model


import com.google.gson.annotations.SerializedName

data class TaskLists(
    @SerializedName("name")
    val name: String,
    @SerializedName("tasks")
    val tasks: List<Task>
)