package com.example.lab4_todoapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

// screen that edits an existing task
class EditTaskActivity : AppCompatActivity() {
    private var taskId: Long = -1L
    private lateinit var db: TaskDB
    private var selectedImageUri: Uri? = null

    // sets up the edit screen and loads the task data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        db = TaskDB(this)

        val titleInput = findViewById<TextInputEditText>(R.id.taskTitleInput)
        val descInput = findViewById<TextInputEditText>(R.id.taskDescriptionInput)
        val dateInput = findViewById<TextInputEditText>(R.id.taskDateInput)
        val saveButton = findViewById<MaterialButton>(R.id.saveTaskButton)
        val backButton = findViewById<MaterialButton>(R.id.bkbutton)
        val selectImageButton = findViewById<MaterialButton>(R.id.uploadImageButton)
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)

        val calendar = Calendar.getInstance()
        var selectedColor = "#FFFFFF"

        // loads the existing task data from the intent
        taskId = intent.getLongExtra("taskId", -1L)
        val title = intent.getStringExtra("taskTitle") ?: ""
        val desc = intent.getStringExtra("taskDesc") ?: ""
        val color = intent.getStringExtra("taskColor") ?: "#FFFFFF"
        val date = intent.getStringExtra("taskDate") ?: ""
        val imageUriString = intent.getStringExtra("taskImageUri")

        titleInput.setText(title)
        descInput.setText(desc)
        dateInput.setText(date)
        selectedColor = color

        // shows existing image if it exists
        if (!imageUriString.isNullOrEmpty()) {
            try {
                selectedImageUri = Uri.parse(imageUriString)
                imagePreview.setImageURI(selectedImageUri)
                imagePreview.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                imagePreview.visibility = View.GONE
            }
        }

        // sets up color picker highlights
        val colorRed = findViewById<View>(R.id.colorRed)
        val colorBlue = findViewById<View>(R.id.colorBlue)
        val colorGreen = findViewById<View>(R.id.colorGreen)
        val colorViews = listOf(colorRed, colorBlue, colorGreen)

        // highlights whichever color is selected
        fun highlightSelected(view: View) {
            colorViews.forEach { it.alpha = 0.4f }
            view.alpha = 1.0f
        }

        when (selectedColor) {
            "#FF6B6B" -> highlightSelected(colorRed)
            "#4D96FF" -> highlightSelected(colorBlue)
            "#1DD1A1" -> highlightSelected(colorGreen)
        }

        colorRed.setOnClickListener { selectedColor = "#FF6B6B"; highlightSelected(colorRed) }
        colorBlue.setOnClickListener { selectedColor = "#4D96FF"; highlightSelected(colorBlue) }
        colorGreen.setOnClickListener { selectedColor = "#1DD1A1"; highlightSelected(colorGreen) }

        // opens date picker when date is clicked
        dateInput.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val picker = DatePickerDialog(
                this,
                { _, y, m, d -> dateInput.setText("$d/${m + 1}/$y") },
                year, month, day
            )
            picker.show()
        }

        // handles picking an image from storage
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                selectedImageUri = uri
                imagePreview.setImageURI(uri)
                imagePreview.visibility = View.VISIBLE
            }
        }

        // opens image picker button
        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        // saves edited task back to DB and returns result
        saveButton.text = "Save Changes"
        saveButton.setOnClickListener {
            val newTitle = titleInput.text?.toString()?.trim().orEmpty()
            val newDesc = descInput.text?.toString()?.trim().orEmpty()
            val newDate = dateInput.text?.toString()?.trim().orEmpty()

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = Task(
                id = taskId,
                title = newTitle,
                description = newDesc,
                color = selectedColor,
                date = newDate,
                imageUri = selectedImageUri?.toString()
            )

            db.update(updatedTask)

            val result = Intent().apply {
                putExtra("updatedTaskId", taskId)
                putExtra("updatedTitle", newTitle)
                putExtra("updatedDesc", newDesc)
                putExtra("updatedColor", selectedColor)
                putExtra("updatedDate", newDate)
                putExtra("updatedImageUri", selectedImageUri?.toString())
            }
            setResult(RESULT_OK, result)
            finish()
        }

        // closes screen without saving
        backButton.text = "Discard"
        backButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
