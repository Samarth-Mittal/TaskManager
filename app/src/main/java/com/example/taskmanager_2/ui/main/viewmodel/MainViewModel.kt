package com.example.taskmanager_2.ui.main.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.taskmanager_2.data.api.ApiClient
import com.example.taskmanager_2.data.api.UserApi
import com.example.taskmanager_2.data.model.*
import com.example.taskmanager_2.utils.Resource
import kotlinx.coroutines.Dispatchers

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
            editor.putString("isProfileSet", "Yes")
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
        members?.forEach {
            memberNames.add(it.name+"("+it.id.toString()+")")
        }


        //Getting all tasks
        val allTasks = body?.statusTaskLists
        val toDoTaskList = allTasks?.get(0)?.tasks
        val inProgressTaskList = allTasks?.get(1)?.tasks
        val completedTaskList = allTasks?.get(2)?.tasks

        val toDoString = mutableSetOf<String>()
        val allToDoString = mutableSetOf<String>()
        val inProgressString = mutableSetOf<String>()
        val allInProgressString = mutableSetOf<String>()
        val completedString = mutableSetOf<String>()
        val allCompletedString = mutableSetOf<String>()

        toDoTaskList?.forEach {
            var item = getTaskDetails(members, it)
            toDoString.add(item)
            allToDoString.add(item)
        }
        inProgressTaskList?.forEach {
            var item = getTaskDetails(members, it)
            inProgressString.add(item)
            allInProgressString.add(item)
        }
        completedTaskList?.forEach {
            var item = getTaskDetails(members, it)
            completedString.add(item)
            allCompletedString.add(item)
        }

        val editor = token.edit()
        editor.putStringSet("TeamMembers", memberNames)
        editor.putStringSet("toDoTasks", toDoString)
        editor.putStringSet("inProgressTasks", inProgressString)
        editor.putStringSet("completedTasks", completedString)
        editor.putStringSet("allToDoTasks", allToDoString)
        editor.putStringSet("allInProgressTasks", allInProgressString)
        editor.putStringSet("allCompletedTasks", allCompletedString)
        editor.commit()

    }

    private fun getTaskDetails(members: List<Member>?, it: Task): String {

        var i6: String
        var i9: String
        if(it.reporterId==null){
            i6="-1"
            i9="null"
        }else {
            i6 = it.reporterId.toString()
            i9 = getReporterName(members, it.reporterId)
        }
        var i7: String
        var i10: String
        if(it.assigneId==null){
            i7 = "-1"
            i10 = "null"
        }else {
            i7 = it.assigneId.id.toString()
            i10 = it.assigneId!!.name
        }
        var item = it.title + "," + it.id + "," + it.status + "," + it.priority + "," + it.description + "," + i6 + "," + i7 + "," + it.planneddate + "," + i9 + "," + i10
        return  item
    }

    private fun getReporterName(members: List<Member>?, reporterId: Int): String {

        var name: String = ""
        members?.forEach lit@{
            if(reporterId==it.id){
                name = it.name
                return@lit
            }
        }
        return name

    }

    fun createTask(task: NewTask, id: String?)= liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val create_task_response = apiService.createTask(task, id)
        if(create_task_response.isSuccessful) {
            emit(Resource.success(create_task_response.body()?.response_string))
        }else{
            emit(Resource.error(create_task_response.body()?.response_string.toString()))
        }
    }

    fun deleteTask(id: String) = liveData(Dispatchers.IO){
        emit(Resource.loading())
        val delete_task_response = apiService.deleteTask(id)
        if(delete_task_response.isSuccessful){
            emit((Resource.success(delete_task_response.body()?.response_string)))
        }else{
            emit(Resource.error(delete_task_response.body()?.response_string.toString()))
        }
    }

    fun updateTask(updatedTask: UpdateTask, id: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val update_task_response = apiService.updateTask(updatedTask, id)
        if(update_task_response.isSuccessful){
            emit((Resource.success(update_task_response.body()?.response_string)))
        }else{
            emit(Resource.error(update_task_response.body()?.response_string.toString()))
        }
    }

    /*fun getSortedTasks(type: Type, id: String, token: SharedPreferences) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val teamDetails = apiService.getSortedTasks(type, id)
        if(teamDetails.isSuccessful){
            parseResponse(teamDetails.body(), token)
            emit(Resource.success(teamDetails.body()?.name))
        }else{
            emit(Resource.error(teamDetails.body()?.id.toString()))
        }

    }*/
}