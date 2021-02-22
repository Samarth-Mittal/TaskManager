package com.example.taskmanager_2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager_2.ui.main.adapter.MainAdapter
import com.example.taskmanager_2.ui.main.adapter.OnTaskClickListener
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeFragment.HomeFragmentCallback, OnTaskClickListener {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val token: SharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        populateTeamIDsUIComponent(token)

        navController = findNavController(R.id.fragmentHost)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        title = "Dashboard"

        if(token.getInt("isFirstLogin", 0)==0){
            var editor = token.edit()
            editor.putInt("isFirstLogin", 1)
            editor.commit()
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    /*interface OnTaskItemClickListener {
        fun onItemClick(task: String?, position: Int)
    }*/

    private fun populateTeamIDsUIComponent(token: SharedPreferences?) {

        val viewModel = MainViewModel()
        if (token != null) {
            viewModel.getTeamIDs(token.getString("userID", token.getString("UserID", "0")), token).observe(this,
                Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "Could not load teams from network",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentHost)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun getSpinners(teamID: Long, taskID: Long, statusID: Long, sortID: Long) {

        getTasks((taskID).toInt())

        populateRV(statusID.toInt())

        /*when(spinnerId){
            1 -> {

            }
            2 -> {
                populateRV(position)
            }
            3 -> {

            }
            4 -> {

            }
        }*/
    }

    override fun goToNewTaskActivity(name: String) {
        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val teamsIDs = token.getStringSet("TeamIDs", mutableSetOf<String>())
        var id = "0"
        teamsIDs?.forEach {
            var teamName = it.substring(0, it.indexOf("("))
            if(teamName.equals(name)){
                id = it.substring(it.indexOf("(")+1, it.indexOf(")"))
            }
        }
        var intent = Intent(this, NewTaskActivity::class.java)
        //intent.putExtra("TeamID", id)
        startActivity(intent)
    }

    private fun getTasks(position: Int) {
        val token: SharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        val TeamMembers = token.getStringSet("TeamMembers", mutableSetOf<String>())
        val memberID: String

        var allToDoString = token.getStringSet("allToDoTasks", mutableSetOf<String>())
        var allInProgressString = token.getStringSet("allInProgressTasks", mutableSetOf<String>())
        var allCompletedString = token.getStringSet("allCompletedTasks", mutableSetOf<String>())
        var toDoString = mutableSetOf<String>()
        var inProgressString = mutableSetOf<String>()
        var completedString = mutableSetOf<String>()

        if(position>=0) {
            val element = TeamMembers?.elementAt(position).toString()
            memberID = element.substring(element.indexOf("(") + 1, element.indexOf(")"))

            allToDoString?.forEach {
                var task = it.split(",")
                if(task[6].equals(memberID)){
                    toDoString.add(it)
                }
            }
            allCompletedString?.forEach {
                var task = it.split(",")
                if(task[6].equals(memberID)){
                    inProgressString.add(it)
                }
            }
            allInProgressString?.forEach {
                var task = it.split(",")
                if(task[6].equals(memberID)){
                    completedString.add(it)
                }
            }
        }else{
            memberID = "-1"
            toDoString = allToDoString!!
            inProgressString = allInProgressString!!
            completedString = allCompletedString!!
        }

        val editor = token.edit()
        editor.putStringSet("toDoTasks", toDoString)
        editor.putStringSet("inProgressTasks", inProgressString)
        editor.putStringSet("completedTasks", completedString)
        editor.commit()

    }

    private fun populateRV(position: Int) {

        when(position){
            0 -> {
                recyclerViewAdapter("toDoTasks")
            }
            1 -> {
                recyclerViewAdapter("inProgressTasks")
            }
            2 -> {
                recyclerViewAdapter("completedTasks")
            }
        }

    }

    private fun recyclerViewAdapter(key: String) {

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        lateinit var adapter: MainAdapter

        recyclerView.addItemDecoration(DividerItemDecoration(this, 0))

        val taskString = token.getStringSet(key, mutableSetOf<String>())
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(taskString?.toList()!!, this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter

    }

    override fun onItemClick(task: String?, position: Int) {
        var intent = Intent(this, TaskActivity::class.java)
        intent.putExtra("TaskInfo", task.toString())
        intent.putExtra("TaskPosition", position)
        startActivity(intent)
    }
}