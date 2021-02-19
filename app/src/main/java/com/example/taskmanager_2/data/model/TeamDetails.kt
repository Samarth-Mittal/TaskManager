package com.example.taskmanager_2.data.model


import com.google.gson.annotations.SerializedName

data class TeamDetails(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lists")
    val statusTaskLists: List<TaskLists>,
    @SerializedName("members")
    val members: List<Member>,
    @SerializedName("name")
    val name: String,
    @SerializedName("tasks")
    val tasks: List<Int>
)