package com.example.taskmanager_2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager_2.ui.main.adapter.MainAdapter
import com.example.taskmanager_2.ui.main.adapter.OnTaskClickListener

class DraftActivity : AppCompatActivity(), OnTaskClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)

        title = "My Drafts"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        populateDraftRecyclerView()
    }

    private fun populateDraftRecyclerView() {
        val token = getSharedPreferences("User", Context.MODE_PRIVATE)
        val recyclerView = findViewById<RecyclerView>(R.id.draftRecyclerView)
        lateinit var adapter: MainAdapter
        var userID = token.getString("UserID", "0")
        var drafts = token.getStringSet("drafts-$userID", mutableSetOf<String>())

        recyclerView.addItemDecoration(DividerItemDecoration(this, 0))

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(drafts?.toList()!!, this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    override fun onItemClick(task: String?, position: Int) {
        var intent = Intent(this, DraftTaskActivity::class.java)
        intent.putExtra("TaskInfo", task.toString())
        intent.putExtra("TaskPosition", position.toString())
        startActivity(intent)
    }
}