package com.example.taskmanager_2

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.UpdateTask
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.activity_task.editTextAssignee
import kotlinx.android.synthetic.main.activity_task.editTextDescription
import kotlinx.android.synthetic.main.activity_task.editTextPlannedDate
import kotlinx.android.synthetic.main.activity_task.editTextPriority
import kotlinx.android.synthetic.main.activity_task.editTextReporter
import kotlinx.android.synthetic.main.activity_task.editTextStatus
import kotlinx.android.synthetic.main.activity_task.editTextTitle

class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        title = "Task Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val task = intent.getStringExtra("TaskInfo")
        val taskList = task.split(",")

        editTextTitle.setText(taskList[0])
        editTextID.setText(taskList[1])
        editTextStatus.setText(taskList[2])
        editTextPriority.setText(taskList[3])
        editTextDescription.setText(taskList[4])
        editTextReporter.setText(taskList[8])
        editTextAssignee.setText(taskList[9])
        editTextPlannedDate.setText(taskList[7])

        buttonEdit.setOnClickListener{

            buttonEdit.isEnabled = false
            buttonDelete.isEnabled = false
            buttonSave.isEnabled = true
            buttonCancel.isEnabled = true
            linearLayoutEditDeleteBtns.visibility = View.GONE
            linearLayoutSaveCancelBtns.visibility = View.VISIBLE

            editTextDescription.isEnabled = true
            editTextStatus.isEnabled = true
            editTextPriority.isEnabled = true
        }

        buttonSave.setOnClickListener{

            buttonCancel.isEnabled = false
            buttonSave.isEnabled = false
            buttonEdit.isEnabled = true
            buttonDelete.isEnabled = true
            linearLayoutSaveCancelBtns.visibility = View.GONE
            linearLayoutEditDeleteBtns.visibility = View.VISIBLE
            editTextDescription.isEnabled = false
            editTextStatus.isEnabled = false
            editTextPriority.isEnabled = false

            var updatedTask = UpdateTask()

            var id = taskList[1]
            updatedTask.description = editTextDescription.text.toString()
            updatedTask.status = editTextStatus.text.toString()
            updatedTask.priority = editTextPriority.text.toString()
            updatedTask.planneddate = taskList[7]
            updatedTask.reporter_id = taskList[5]
            updatedTask.assigne_id = taskList[6]

            val viewModel = MainViewModel()
            viewModel.updateTask(updatedTask, id).observe(this, Observer { networkResource ->
                when (networkResource.status) {
                    Status.LOADING -> {
                        Toast.makeText(this, " Saving...", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        val message = networkResource.data
                        message?.let {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Could not save the details", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }

        buttonCancel.setOnClickListener{

            buttonCancel.isEnabled = false
            buttonSave.isEnabled = false
            buttonEdit.isEnabled = true
            buttonDelete.isEnabled = true
            linearLayoutSaveCancelBtns.visibility = View.GONE
            linearLayoutEditDeleteBtns.visibility = View.VISIBLE

            editTextTitle.setText(taskList[0])
            editTextID.setText(taskList[1])
            editTextStatus.setText(taskList[2])
            editTextPriority.setText(taskList[3])
            editTextDescription.setText(taskList[4])
            editTextReporter.setText(taskList[8])
            editTextAssignee.setText(taskList[9])
            editTextPlannedDate.setText(taskList[7])

            editTextTitle.isEnabled = false
            editTextDescription.isEnabled = false
            editTextStatus.isEnabled = false
            editTextPriority.isEnabled = false
        }

        buttonDelete.setOnClickListener{

            //alert
            var builer = AlertDialog.Builder(this)
            builer.setTitle("Confirm delete")
            builer.setMessage("Are you sure you want to delete this task?")

            builer.setPositiveButton("Yes, delete task.", DialogInterface.OnClickListener{ dialog, id ->

                val viewModel = MainViewModel()
                viewModel.deleteTask(editTextID.text.toString()).observe(this, Observer { networkResource ->
                    when (networkResource.status) {
                        Status.LOADING -> {
                            Toast.makeText(this, " Deleting Task", Toast.LENGTH_SHORT).show()
                        }
                        Status.SUCCESS -> {
                            val message = networkResource.data
                            message?.let {
                                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(this, "Could not delete task", Toast.LENGTH_SHORT).show()
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
}