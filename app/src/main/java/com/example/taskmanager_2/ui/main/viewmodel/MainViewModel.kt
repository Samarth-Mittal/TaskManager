package com.example.taskmanager_2.ui.main.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.taskmanager_2.data.api.ApiClient
import com.example.taskmanager_2.data.api.UserApi
import com.example.taskmanager_2.data.model.LoginUser
import com.example.taskmanager_2.data.model.SignUpUser
import com.example.taskmanager_2.data.model.TeamDetails
import com.example.taskmanager_2.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.net.ResponseCache

class MainViewModel : ViewModel() {

    val apiService = ApiClient.createService(UserApi::class.java)

    fun doLogin(loginUser: LoginUser, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val login_response = apiService.doLogin(loginUser)
        if(login_response.isSuccessful) {
            val editor = token.edit()
            editor.putString("UserID", login_response.body()?.id.toString())
            editor.commit()
            emit(Resource.success(login_response.body()?.response_string))
        }else{
            emit(Resource.error(login_response.body()?.response_string.toString()))
        }
    }

    fun doRegistration(signUpUser: SignUpUser) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val signup_response = apiService.doRegister(signUpUser)
        if(signup_response.isSuccessful){
            emit(Resource.success(signup_response.body()?.response_string))
        }
    }

    fun getUser(id: String?, token: SharedPreferences) = liveData(Dispatchers.IO){
        emit(Resource.loading())
        val user = apiService.getUser(id)
        if(user.isSuccessful){
            val editor = token.edit()
            editor.putString("Name", user.body()?.name)
            editor.putString("Email", user.body()?.email)
            editor.putString("UserID", user.body()?.id)
            editor.putString("Phone", user.body()?.phone)
            editor.commit()
            emit(Resource.success(user.body()?.name))
        }else{
            emit(Resource.error(user.body()?.email.toString()))
        }
    }

    fun getTeamIDs(id: String?, token: SharedPreferences) = liveData(Dispatchers.IO){
        emit(Resource.loading())
        val teams = apiService.getTeamIDs(id)
        if(teams.isSuccessful){
            var teamIds = mutableSetOf<String>()
            teams.body()?.forEach {
                teamIds.add(it.name+"("+ it.id.toString()+ ")")
            }
            val editor = token.edit()
            editor.putStringSet("TeamIDs", teamIds)
            editor.commit()
            if(teams.body()?.isEmpty()!!){
                emit(Resource.success("Success, but with no data"))
            }else {
                emit(Resource.success(teams.body()?.get(0)?.name.toString()))
            }
        }else{
            emit(Resource.error(teams.body()?.get(0)?.id.toString()))
        }
    }

    fun getTeamDetails(id: String?, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val teamDetails = apiService.getTeamDetails(id)
        if(teamDetails.isSuccessful){
            parseResponse(teamDetails.body(), token)
            emit(Resource.success(teamDetails.body()?.name))
        }else{
            emit(Resource.error(teamDetails.body()?.id.toString()))
        }

    }

    private fun parseResponse(body: TeamDetails?, token: SharedPreferences) {

        //Getting team members
        val members = body?.members
        var memberNames = mutableSetOf<String>()
        memberNames.add("All(0)")
        members?.forEach {
            memberNames.add(it.name+"("+it.id.toString()+")")
        }

        //Getting all tasks
        val allTasks = body?.statusTaskLists
        val toDoTaskList = allTasks?.get(0)?.tasks
        val inProgressTaskList = allTasks?.get(1)?.tasks
        val completedTaskList = allTasks?.get(2)?.tasks

        val toDoString = mutableSetOf<String>()
        val inProgressString = mutableSetOf<String>()
        val completedString = mutableSetOf<String>()

        toDoTaskList?.forEach {
            toDoString.add(it.title+","+it.id+","+it.status+","+it.priority+","+it.description+","+it.reporterId+","+it.assigneId+","+it.planneddate)
        }
        inProgressTaskList?.forEach {
            inProgressString.add(it.title+","+it.id+","+it.status+","+it.priority+","+it.description+","+it.reporterId+","+it.assigneId+","+it.planneddate)
        }
        completedTaskList?.forEach {
            completedString.add(it.title+","+it.id+","+it.status+","+it.priority+","+it.description+","+it.reporterId+","+it.assigneId+","+it.planneddate)
        }

        val editor = token.edit()
        editor.putStringSet("TeamMembers", memberNames)
        editor.putStringSet("toDoTasks", toDoString)
        editor.putStringSet("inProgressTasks", inProgressString)
        editor.putStringSet("completedTasks", completedString)
        editor.commit()

    }
}