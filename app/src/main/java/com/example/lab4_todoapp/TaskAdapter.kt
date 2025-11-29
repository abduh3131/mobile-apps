package com.example.lab4_todoapp

import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCheckedChanged: (task: Task, isChecked: Boolean) -> Unit,
    private val onTaskLongPressed: (task: Task, position: Int) -> Unit,
    private val onTaskClicked: (task: Task, position: Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskVH>() {

    // keeps a copy of all tasks for search filtering
    private var fullList: MutableList<Task> = ArrayList(tasks)

    inner class TaskVH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.taskTitle)
        val desc: TextView = v.findViewById(R.id.taskDesc)
        val check: CheckBox = v.findViewById(R.id.taskCheck)
        val date: TextView = v.findViewById(R.id.taskDate)
        val image: ImageView = v.findViewById(R.id.taskImage)
    }

    // creates ViewHolder for each row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskVH(view)
    }

    // binds task data to each row
    override fun onBindViewHolder(h: TaskVH, position: Int) {
        val task = tasks[position]

        val cardView = h.itemView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.taskCard)
        cardView.setCardBackgroundColor(android.graphics.Color.parseColor(task.color))

        h.title.text = task.title
        h.desc.text = task.description
        h.date.text = task.date

        // handles showing image if attached
        if (!task.imageUri.isNullOrEmpty()) {
            h.image.visibility = View.VISIBLE
            try {
                h.image.setImageURI(Uri.parse(task.imageUri))
            } catch (_: SecurityException) {
                h.image.visibility = View.GONE
            }
        } else {
            h.image.visibility = View.GONE
        }

        // handles checking and strike through
        h.check.setOnCheckedChangeListener(null)
        h.check.isChecked = task.isDone
        h.title.paintFlags =
            if (task.isDone) h.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else h.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        // saves done state changes
        h.check.setOnCheckedChangeListener { _, checked ->
            h.title.paintFlags =
                if (checked) h.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else h.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            val idx = h.bindingAdapterPosition
            if (idx != RecyclerView.NO_POSITION) {
                tasks[idx] = task.copy(isDone = checked)
                val fullIdx = fullList.indexOfFirst { it.id == task.id }
                if (fullIdx != -1) fullList[fullIdx] = task.copy(isDone = checked)
            }

            onTaskCheckedChanged(task, checked)
        }

        // opens edit on click
        h.itemView.setOnClickListener {
            val pos = h.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onTaskClicked(tasks[pos], pos)
        }

        // deletes on long press (fixed to avoid crash)
        h.itemView.setOnLongClickListener {
            val pos = h.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                // get the task before list is changed
                val removed = tasks[pos]

                // let activity handle DB deletion and adapter notification
                onTaskLongPressed(removed, pos)

                // keep fullList in sync for search
                val idxInFull = fullList.indexOfFirst { it.id == removed.id }
                if (idxInFull != -1) {
                    fullList.removeAt(idxInFull)
                }
            }
            true
        }
    }

    override fun getItemCount() = tasks.size

    // updates whole list after DB change
    fun updateTasks(newList: List<Task>) {
        tasks.clear()
        tasks.addAll(newList)
        fullList.clear()
        fullList.addAll(newList)
        notifyDataSetChanged()
    }

    // filters list when user types in search bar
    fun filterTasks(query: String) {
        val filtered = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
        }

        tasks.clear()
        tasks.addAll(filtered)
        notifyDataSetChanged()
    }
}
