// app/src/main/java/com/example/asi1/EmiActivity.kt
package com.example.asi1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlin.math.pow

class EmiActivity : AppCompatActivity() {
    private val prefsName = "asi1_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emi)

        val toolbar = findViewById<Toolbar?>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_24)
        toolbar?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val etLoan = findViewById<EditText>(R.id.etLoan)
        val etInterest = findViewById<EditText>(R.id.etInterest)
        val etYears = findViewById<EditText>(R.id.etYears)
        val etMonths = findViewById<EditText>(R.id.etMonths)
        val btnCalc = findViewById<Button>(R.id.btnCalc)
        val tvEmi = findViewById<TextView>(R.id.tvEmi)

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        etLoan.setText(prefs.getString("loan", ""))
        etInterest.setText(prefs.getString("interest", ""))
        etYears.setText(prefs.getString("years", ""))
        etMonths.setText(prefs.getString("months", ""))
        val savedEmi = prefs.getFloat("emi", 0f)
        if (savedEmi > 0f) {
            tvEmi.text = getString(R.string.emi_result) + " " + String.format("%,.2f", savedEmi.toDouble())
        }

        btnCalc.setOnClickListener {
            val loan = etLoan.text.toString().trim().toDoubleOrNull() ?: 0.0
            val annualRate = etInterest.text.toString().trim().toDoubleOrNull() ?: 0.0
            val y = etYears.text.toString().trim().toIntOrNull() ?: 0
            val m = etMonths.text.toString().trim().toIntOrNull() ?: 0
            val n = (y * 12) + m
            val r = (annualRate / 100.0) / 12.0
            val emi = when {
                n <= 0 -> 0.0
                r == 0.0 -> if (n == 0) 0.0 else loan / n
                else -> {
                    val p = (1.0 + r).pow(n)
                    loan * r * p / (p - 1.0)
                }
            }
            tvEmi.text = getString(R.string.emi_result) + " " + String.format("%,.2f", emi)
            prefs.edit()
                .putString("loan", etLoan.text.toString())
                .putString("interest", etInterest.text.toString())
                .putString("years", etYears.text.toString())
                .putString("months", etMonths.text.toString())
                .putFloat("emi", emi.toFloat())
                .apply()
        }
    }
}
