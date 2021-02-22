package com.example.taskmanager_2

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.taskmanager_2.data.model.NewTask
import com.example.taskmanager_2.ui.main.viewmodel.MainViewModel
import com.example.taskmanager_2.utils.Status
import kotlinx.android.synthetic.main.activity_new_task.*

class NewTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        title = "Create Task"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonCreateTask.setOnClickListener{

            viewModel = MainViewModel()

            var task = NewTask()

            val editTextTeamID = editTextTeamID.text.toString()

            task.title = editTextTitle.text.toString()
            task.description = editTextDescription.text.toString()
            task.status = editTextStatus.text.toString()
            task.priority = editTextPriority.text.toString()
            task.assigne_id = editTextAssignee.text.toString()
            task.reporter_id = editTextReporter.text.toString()
            task.planneddate = editTextPlannedDate.text.toString()

            viewModel.createTask(task, editTextTeamID).observe(this, Observer { networkResource ->
                when (networkResource.status) {
                    Status.LOADING -> {
                        Toast.makeText(this, "Creating Task", Toast.LENGTH_SHORT).show()
                    }
                    Status.SUCCESS -> {
                        val message = networkResource.data
                        message?.let {
                            Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)

                            startActivity(intent)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Could not create task", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            startActivity(Intent(this, HomeActivity::class.java))
        }

        buttonCancelTask.setOnClickListener{
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm cancel")
            builder.setMessage("Are you sure you want to cancel this task creation?")
            builder.setPositiveButton("Yes, discard the task", DialogInterface.OnClickListener{ dialog, id ->
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                dialog.cancel()
            })
            builder.setNegativeButton("No, save to drafts", DialogInterface.OnClickListener{ dialog, id ->
                val token = getSharedPreferences("User", Context.MODE_PRIVATE)
                val editor = token.edit()

                val editTextTeamID = editTextTeamID.text.toString()

                val userID = token.getString("UserID", "0")
                var drafts = token.getStringSet("drafts-$userID", mutableSetOf<String>())
                //var file = cacheDir
                var task =
                    editTextTeamID + "," + editTextTitle.text.toString() + "," + editTextDescription.text.toString() + "," + editTextStatus.text.toString() + "," + editTextPriority.text.toString() + "," + editTextAssignee.text.toString() + "," + editTextReporter.text.toString() + "," + editTextPlannedDate.text.toString()
                drafts?.add(task)
                editor.putStringSet("drafts-$userID", drafts)
                editor.commit()
                dialog.cancel()
                Toast.makeText(this, "Saved to drafts.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            })
            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            })
            var alert = builder.create()
            alert.show()
        }

    }
}