package com.example.taskmanager_2

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.NewTask
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_draft_task.*

class DraftTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft_task)
        title = "Task Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val task = intent.getStringExtra("TaskInfo")
        val taskList = task.split(",")
        val token = getSharedPreferences("User", Context.MODE_PRIVATE)

        editTextDraftTeamID.setText(taskList[0])
        editTextDraftTitle.setText(taskList[1])
        editTextDraftDescription.setText(taskList[2])
        editTextDraftStatus.setText(taskList[3])
        editTextDraftPriority.setText(taskList[4])
        editTextDraftAssignee.setText(taskList[5])
        editTextDraftReporter.setText(taskList[6])
        editTextDraftPlannedDate.setText(taskList[7])

        buttonDraftEdit.setOnClickListener{

            title = "Edit Task Details"

            editTextDraftTeamID.isEnabled = true
            editTextDraftTitle.isEnabled = true
            editTextDraftDescription.isEnabled = true
            editTextDraftStatus.isEnabled = true
            editTextDraftPriority.isEnabled = true
            editTextDraftPlannedDate.isEnabled = true
            editTextDraftAssignee.isEnabled = true
            editTextDraftReporter.isEnabled = true

            linearLayoutDraftEditDeleteBtns.visibility = View.GONE
            linearLayoutDraftSaveCancelBtns.visibility = View.VISIBLE

            buttonDraftDelete.isEnabled = false
            buttonDraftEdit.isEnabled = false
            buttonDraftSave.isEnabled = true
            buttonDraftCancel.isEnabled = true

        }

        buttonDraftSave.setOnClickListener{
            onSaveOrCancelBtnClick()

            val userId = token.getString("UserID", "0")
            val drafts = token.getStringSet("drafts-$userId", mutableSetOf())
            var newTask = editTextDraftTeamID.text.toString() + "," + editTextDraftTitle.text.toString() + "," + editTextDraftDescription.text.toString() + "," + editTextDraftStatus.text.toString() + "," + editTextDraftPriority.text.toString() + "," + editTextDraftAssignee.text.toString() + "," + editTextDraftReporter.text.toString() + "," + editTextDraftPlannedDate.text.toString()
            drafts?.remove(task)
            drafts?.add(newTask)
            val editor = token.edit()
            editor.putStringSet("drafts-$userId", drafts)
            editor.commit()
            startActivity(Intent(this, DraftActivity::class.java))
            finish()
        }

        buttonDraftCancel.setOnClickListener{
            onSaveOrCancelBtnClick()
        }

        buttonDraftDelete.setOnClickListener{

            var builer = AlertDialog.Builder(this)
            builer.setTitle("Confirm delete")
            builer.setMessage("Are you sure you want to delete this task?")

            builer.setPositiveButton("Yes, delete task.", DialogInterface.OnClickListener{ dialog, id ->

                val userID = token.getString("UserID", "0")
                val drafts = token.getStringSet("drafts-$userID", mutableSetOf())
                var item = editTextDraftTeamID.text.toString() + "," + editTextDraftTitle.text.toString() + "," + editTextDraftDescription.text.toString() + "," + editTextDraftStatus.text.toString() + "," + editTextDraftPriority.text.toString() + "," + editTextDraftAssignee.text.toString() + "," + editTextDraftReporter.text.toString() + "," + editTextDraftPlannedDate.text.toString()
                drafts?.remove(item)
                val intent = Intent(this, DraftActivity::class.java)
                startActivity(intent)
                finish()
                dialog.cancel()
            })
            builer.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            })
            builer.create().show()
        }

        buttonDraftCreate.setOnClickListener{
            var builer = AlertDialog.Builder(this)
            builer.setTitle("Confirm task creation")
            builer.setMessage("Are you sure you want to create this task?")

            builer.setPositiveButton("Yes, create task.", DialogInterface.OnClickListener{ dialog, id ->

                var newTask = NewTask()
                val viewModel = MainViewModel()

                newTask.title = editTextDraftTitle.text.toString()
                newTask.description = editTextDraftDescription.text.toString()
                newTask.status = editTextDraftStatus.text.toString()
                newTask.priority = editTextDraftPriority.text.toString()
                newTask.planneddate = editTextDraftPlannedDate.text.toString()
                newTask.assigne_id = editTextDraftAssignee.text.toString()
                newTask.reporter_id = editTextDraftReporter.text.toString()

                val teamID = editTextDraftTeamID.text.toString()

                var userID = token.getString("UserID", "0")
                var drafts = token.getStringSet("drafts-$userID", mutableSetOf<String>())
                viewModel.createTask(newTask, teamID).observe(this, Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                            Toast.makeText(this, "Creating Task", Toast.LENGTH_SHORT).show()
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                var item = editTextDraftTeamID.text.toString() + "," + editTextDraftTitle.text.toString() + "," + editTextDraftDescription.text.toString() + "," + editTextDraftStatus.text.toString() + "," + editTextDraftPriority.text.toString() + "," + editTextDraftAssignee.text.toString() + "," + editTextDraftReporter.text.toString() + "," + editTextDraftPlannedDate.text.toString()
                                drafts?.remove(item)
                                val editor = token.edit()
                                editor.putStringSet("drafts-$userID", drafts)
                                editor.commit()
                                startActivity(intent)
                                finish()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(this, "Could not create task", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                dialog.cancel()
            })
            builer.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            })
            builer.create().show()

        }
    }

    private fun onSaveOrCancelBtnClick() {
        title = "Task Details"

        editTextDraftTeamID.isEnabled = false
        editTextDraftTitle.isEnabled = false
        editTextDraftDescription.isEnabled = false
        editTextDraftStatus.isEnabled = false
        editTextDraftPriority.isEnabled = false
        editTextDraftPlannedDate.isEnabled = false
        editTextDraftAssignee.isEnabled = false
        editTextDraftReporter.isEnabled = false

        linearLayoutDraftEditDeleteBtns.visibility = View.VISIBLE
        linearLayoutDraftSaveCancelBtns.visibility = View.GONE

        buttonDraftDelete.isEnabled = true
        buttonDraftEdit.isEnabled = true
        buttonDraftSave.isEnabled = false
        buttonDraftCancel.isEnabled = false
    }
}