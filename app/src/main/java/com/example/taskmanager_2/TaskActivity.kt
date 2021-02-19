package com.example.taskmanager_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        textViewTitle.text = intent.getStringExtra("title")
        textViewID.text = intent.getStringExtra("id")
        textViewStatus.text = intent.getStringExtra("status")
        textViewPriority.text = intent.getStringExtra("priority")
        textViewDescription.text = intent.getStringExtra("desc")
        textViewReporter.text = intent.getStringExtra("reporterID")
        textViewAssignee.text = intent.getStringExtra("assigneeID")
        textViewPlannedDate.text = intent.getStringExtra("plannedDate")
    }
}