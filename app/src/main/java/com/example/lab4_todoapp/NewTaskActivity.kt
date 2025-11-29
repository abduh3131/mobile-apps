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

// screen that creates a brand new task
class NewTaskActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null

    // sets up the new task form and handles save
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val titleInput = findViewById<TextInputEditText>(R.id.taskTitleInput)
        val descInput = findViewById<TextInputEditText>(R.id.taskDescriptionInput)
        val dateInput = findViewById<TextInputEditText>(R.id.taskDateInput)
        val saveButton = findViewById<MaterialButton>(R.id.saveTaskButton)
        val backButton = findViewById<MaterialButton>(R.id.bkbutton)
        val selectImageButton = findViewById<MaterialButton>(R.id.uploadImageButton)
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)

        val calendar = Calendar.getInstance()
        var selectedColor = "#FFFFFF"

        val colorRed = findViewById<View>(R.id.colorRed)
        val colorBlue = findViewById<View>(R.id.colorBlue)
        val colorGreen = findViewById<View>(R.id.colorGreen)
        val colorViews = listOf(colorRed, colorBlue, colorGreen)

        // clears any old date text
        dateInput.text?.clear()

        // highlights the picked color
        fun highlightSelected(view: View) {
            colorViews.forEach { it.alpha = 0.4f }
            view.alpha = 1.0f
        }

        colorRed.setOnClickListener {
            selectedColor = "#FF6B6B";
            highlightSelected(colorRed) }
        colorBlue.setOnClickListener {
            selectedColor = "#4D96FF";
            highlightSelected(colorBlue) }
        colorGreen.setOnClickListener {
            selectedColor = "#1DD1A1";
            highlightSelected(colorGreen) }

        // opens date picker dialog when date field is clicked
        dateInput.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d -> dateInput.setText("$d/${m + 1}/$y") },
                year, month, day
            )
            datePicker.show()
        }

        // handles picking an image and keeping permission
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    // keeps read permission so app can load image later
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                    imagePreview.visibility = View.VISIBLE
                }
            }

        // opens gallery/file picker for image
        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        // closes screen without saving
        backButton.setOnClickListener { finish() }

        // saves new task and sends it back to MainActivity
        saveButton.setOnClickListener {
            val title = titleInput.text?.toString()?.trim().orEmpty()
            val desc = descInput.text?.toString()?.trim().orEmpty()
            val date = dateInput.text?.toString()?.trim().orEmpty()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = Intent().apply {
                putExtra("taskTitle", title)
                putExtra("taskDesc", desc)
                putExtra("taskDate", date)
                putExtra("taskColor", selectedColor)
                putExtra("taskImageUri", selectedImageUri?.toString())
            }

            setResult(RESULT_OK, result)
            finish()
        }
    }
}
