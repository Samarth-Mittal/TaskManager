package com.example.taskmanager_2.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager_2.DraftActivity
import com.example.taskmanager_2.R
import kotlinx.android.synthetic.main.layout_task_card.view.*

class MainAdapter(
    private val tasks: List<String?>,
    var clickListener: OnTaskClickListener
): RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: String?) {
            val values = task?.split(",")
            itemView.apply {
                textViewTitle.text = values?.get(0)
                textViewID.text = values?.get(1)
                textViewPriority.text = values?.get(2)
                textViewPlannedDate.text = values?.get(3)
            }
        }

        fun initialize(task: String?, clickListener: OnTaskClickListener) {
            itemView.setOnClickListener{
                clickListener.onItemClick(task, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_task_card, parent, false))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(tasks.elementAt(position))
        holder.initialize(tasks.elementAt(position), clickListener)
    }

}

interface OnTaskClickListener{
    fun onItemClick(task: String?, position: Int)
}