package com.example.asi1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlin.math.abs

class SummaryActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private val prefsName = "asi1_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        val toolbar = findViewById<Toolbar?>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_24)
        toolbar?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val tvIncome = findViewById<TextView>(R.id.tvIncome)
        val tvEmi = findViewById<TextView>(R.id.tvEmi)
        val tvRec = findViewById<TextView>(R.id.tvRec)
        val tvVar = findViewById<TextView>(R.id.tvVar)
        val tvOutflow = findViewById<TextView>(R.id.tvOutflow)
        val tvNetTitle = findViewById<TextView>(R.id.tvNetTitle)
        val tvNetValue = findViewById<TextView>(R.id.tvNetValue)

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val income = prefs.getString("income", "0")?.toDoubleOrNull() ?: 0.0
        val emi = prefs.getFloat("emi", 0f).toDouble()

        db = AppDb(this)
        val totals = db.computeTotals()
        val recurring = totals.first
        val variable = totals.second
        val outflow = emi + recurring + variable
        val net = income - outflow

        tvIncome.text = getString(R.string.summary_income) + " " + String.format("%,.2f", income)
        tvEmi.text = getString(R.string.summary_emi) + " " + String.format("%,.2f", emi)
        tvRec.text = getString(R.string.summary_recurring) + " " + String.format("%,.2f", recurring)
        tvVar.text = getString(R.string.summary_variable) + " " + String.format("%,.2f", variable)
        tvOutflow.text = getString(R.string.summary_total_outflow) + " " + String.format("%,.2f", outflow)
        tvNetTitle.text = if (net >= 0) getString(R.string.summary_savings) else getString(R.string.summary_deficit)
        tvNetValue.text = String.format("%,.2f", abs(net))
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
