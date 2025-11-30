package com.example.lab4_todoapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.abs

// main screen that shows the list of tasks
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDB
    private var tasks = mutableListOf<Task>()
    private var showOnlyPending = false
    private var currentQuery = ""
    private lateinit var searchField: TextInputEditText
    private lateinit var tasksContainer: MaterialCardView
    private lateinit var rootView: View

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var accelSensor: Sensor? = null
    private var accel = 0f
    private var accelCurrent = 0f
    private var accelLast = 0f
    private var lastShakeTime = 0L

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
                        refreshList()
                    }
                }
            }
        }

    // handles result when a task is edited
    private val editTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                val data = res.data ?: return@registerForActivityResult
                val deletedId = data.getLongExtra("deletedTaskId", -1L)
                if (deletedId != -1L) {
                    val deleteIndex = tasks.indexOfFirst { it.id == deletedId }
                    if (deleteIndex != -1) {
                        tasks.removeAt(deleteIndex)
                        refreshList()
                    }
                    return@registerForActivityResult
                }
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
                        refreshList()
                    }
                }
            }
        }

    // sets up the main list, buttons, and search
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        rootView = findViewById(R.id.main)
        tasksContainer = findViewById(R.id.tasksContainer)
        db = TaskDB(this)
        tasks = db.all()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(
            tasks = mutableListOf(),
            onTaskCheckedChanged = { task, checked ->
                // updates done state when checkbox is clicked
                task.id?.let { db.setDone(it, checked) }
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index != -1) tasks[index] = tasks[index].copy(isDone = checked)
                if (showOnlyPending && checked) refreshList()
            },
            onTaskLongPressed = { task, pos ->
                // deletes task from DB and list on long press
                task.id?.let {
                    if (db.delete(it) > 0) {
                        val idx = tasks.indexOfFirst { t -> t.id == task.id }
                        if (idx != -1) {
                            tasks.removeAt(idx)
                            refreshList()
                        }
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

        refreshList()

        recyclerView.adapter = adapter

        val addBtn = findViewById<MaterialButton>(R.id.button)
        val backBtn = findViewById<MaterialButton>(R.id.bkbutton)
        searchField = findViewById(R.id.searchBar)

        // filters tasks as user types in the search bar
        searchField.addTextChangedListener { text ->
            val query = text?.toString().orEmpty().trim()
            currentQuery = query
            adapter.filterTasks(query)
        }

        // opens new task screen
        addBtn.setOnClickListener {
            val i = Intent(this, NewTaskActivity::class.java)
            newTaskLauncher.launch(i)
        }

        // closes the app and goes back
        backBtn.setOnClickListener { finish() }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    //refreshes the list respecting filters and search
    private fun refreshList() {
        val baseList = if (showOnlyPending) tasks.filter { !it.isDone } else tasks
        adapter.updateTasks(baseList)
        if (currentQuery.isNotEmpty()) {
            adapter.filterTasks(currentQuery)
        }
    }

    //handles visual tweaks from light sensor
    private fun applyLightMood(lux: Float) {
        if (lux < 15f) {
            rootView.setBackgroundColor(Color.parseColor("#101820"))
            tasksContainer.cardElevation = 2f
        } else {
            rootView.background = ContextCompat.getDrawable(this, R.drawable.main_background)
            tasksContainer.cardElevation = 8f
        }
    }

    //toggles pending filter when shake detected
    private fun togglePendingFilter() {
        showOnlyPending = !showOnlyPending
        refreshList()
        val msg = if (showOnlyPending) "Showing only pending tasks" else "Showing all tasks"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private val lightListener = object : SensorEventListener {
        //updates UI when light sensor changes
        override fun onSensorChanged(event: SensorEvent) {
            val lux = event.values.firstOrNull() ?: return
            applyLightMood(lux)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    private val accelListener = object : SensorEventListener {
        //detects shake gesture
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            accelLast = accelCurrent
            accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = abs(accelCurrent - accelLast)
            accel = accel * 0.9f + delta
            val now = System.currentTimeMillis()
            if (accel > 12 && now - lastShakeTime > 1200) {
                lastShakeTime = now
                togglePendingFilter()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    //registers sensors on resume
    override fun onResume() {
        super.onResume()
        lightSensor?.let { sensorManager.registerListener(lightListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        accelSensor?.let { sensorManager.registerListener(accelListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    //unregisters sensors to save battery
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightListener)
        sensorManager.unregisterListener(accelListener)
    }
}
