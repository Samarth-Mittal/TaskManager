package com.example.taskmanager_2

import android.content.Context
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
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeFragment.HomeFragmentCallback {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    //private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        title = "Dashboard"

        val token: SharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)

        populateTeamIDsUIComponent(token)

        navController = findNavController(R.id.fragmentHost)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)




    }

    interface OnTaskItemClickListener {
        fun onItemClick(task: String?, position: Int)
    }

    private fun populateTeamIDsUIComponent(token: SharedPreferences?) {

        val viewModel = MainViewModel()
        if (token != null) {
            viewModel.getTeamIDs(token.getString("userID", token.getString("UserID", "0")), token).observe(this,
                Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                            Toast.makeText(this, "loading data from network", Toast.LENGTH_SHORT)
                                .show()
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                                Toast.makeText(this, "HERE: "+message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "error loading data from network",
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

    override fun getSpinners(spinner: Spinner, spinnerId: Int, position: Int) {
        when(spinnerId){
            1 -> {

            }
            2 -> {
                populateRV(position)
            }
        }
    }

    private fun populateRV(position: Int) {

        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        lateinit var adapter: MainAdapter


        when(position){
            0 -> {
                val toDoString = token.getStringSet("toDoTasks", mutableSetOf<String>())
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = MainAdapter(toDoString!!, this)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )
                recyclerView.adapter = adapter
            }
            1 -> {
                val inProgressString = token.getStringSet("inProgressTasks", mutableSetOf<String>())
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = MainAdapter(inProgressString!!, this)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )
                recyclerView.adapter = adapter
            }
            2 -> {
                val completedString = token.getStringSet("completedTasks", mutableSetOf<String>())
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter = MainAdapter(completedString!!, this)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )
                recyclerView.adapter = adapter
            }
        }

    }

    /*override fun onItemClick(task: String?, position: Int) {
        val intent = Intent(this, TaskActivity::class.java)
        val values = task?.split(",")
        intent.putExtra("title", values?.get(0))
        intent.putExtra("id", values?.get(1))
        intent.putExtra("status", values?.get(2))
        intent.putExtra("priority", values?.get(3))
        intent.putExtra("desc", values?.get(4))
        intent.putExtra("reporterID", values?.get(5))
        intent.putExtra("assigneeID", values?.get(6))
        intent.putExtra("plannedDate", values?.get(7))
        startActivity(intent)
    }*/
}