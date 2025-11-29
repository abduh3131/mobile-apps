package com.example.lab4_todoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

// main screen that shows the list of tasks
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDB
    private var tasks = mutableListOf<Task>()

    // handles result when a new task is created
    private val newTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                val data = res.data
                val title = data?.getStringExtra("taskTitle")?.trim()
                val desc = data?.getStringExtra("taskDesc")?.trim() ?: ""
                val color = data?.getStringExtra("taskColor") ?: "#FFFFFF"
                val date = data?.getStringExtra("taskDate") ?: ""
                val imageUri = data?.getStringExtra("taskImageUri")

                if (!title.isNullOrEmpty()) {
                    val t = Task(title = title, description = desc, color = color, date = date, imageUri = imageUri)
                    val id = db.insert(t)
                    if (id != -1L) {
                        tasks.add(t.copy(id = id))
                        adapter.notifyItemInserted(tasks.size - 1)
                    }
                }
            }
        }

    // handles result when a task is edited
    private val editTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                val data = res.data ?: return@registerForActivityResult
                val id = data.getLongExtra("updatedTaskId", -1L)
                val title = data.getStringExtra("updatedTitle") ?: ""
                val desc = data.getStringExtra("updatedDesc") ?: ""
                val color = data.getStringExtra("updatedColor") ?: "#FFFFFF"
                val date = data.getStringExtra("updatedDate") ?: ""
                val imageUri = data.getStringExtra("updatedImageUri")

                val index = tasks.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updatedTask = tasks[index].copy(
                        title = title,
                        description = desc,
                        color = color,
                        date = date,
                        imageUri = imageUri
                    )
                    if (db.update(updatedTask) > 0) {
                        tasks[index] = updatedTask
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }

    // sets up the main list, buttons, and search
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = TaskDB(this)
        tasks = db.all()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(
            tasks = tasks,
            onTaskCheckedChanged = { task, checked ->
                // updates done state when checkbox is clicked
                task.id?.let { db.setDone(it, checked) }
            },
            onTaskLongPressed = { task, pos ->
                // deletes task from DB and list on long press
                task.id?.let {
                    if (db.delete(it) > 0) {
                        tasks.removeAt(pos)
                        adapter.notifyItemRemoved(pos)
                    }
                }
            },
            onTaskClicked = { task, _ ->
                // opens edit screen when a task is tapped
                val i = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("taskId", task.id)
                    putExtra("taskTitle", task.title)
                    putExtra("taskDesc", task.description)
                    putExtra("taskColor", task.color)
                    putExtra("taskDate", task.date)
                    putExtra("taskImageUri", task.imageUri)
                }
                editTaskLauncher.launch(i)
            }
        )

        recyclerView.adapter = adapter

        val addBtn = findViewById<MaterialButton>(R.id.button)
        val backBtn = findViewById<MaterialButton>(R.id.bkbutton)
        val searchField = findViewById<TextInputEditText>(R.id.searchBar)

        // filters tasks as user types in the search bar
        searchField.addTextChangedListener { text ->
            val query = text?.toString().orEmpty().trim()
            adapter.filterTasks(query)
        }

        // opens new task screen
        addBtn.setOnClickListener {
            val i = Intent(this, NewTaskActivity::class.java)
            newTaskLauncher.launch(i)
        }

        // closes the app and goes back
        backBtn.setOnClickListener { finish() }
    }
}
