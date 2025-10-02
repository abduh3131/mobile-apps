// app/src/main/java/com/example/asi1/MainActivity.kt
package com.example.asi1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEmi = findViewById<Button>(R.id.btnEmi)
        val btnIncomeExpense = findViewById<Button>(R.id.btnIncomeExpense)
        val btnSummary = findViewById<Button>(R.id.btnSummary)

        btnEmi.setOnClickListener { startActivity(Intent(this, EmiActivity::class.java)) }
        btnIncomeExpense.setOnClickListener { startActivity(Intent(this, IncomeActivity::class.java)) }
        btnSummary.setOnClickListener { startActivity(Intent(this, SummaryActivity::class.java)) }
    }
}
