package com.example.asi1

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class IncomeActivity : AppCompatActivity() {
    private lateinit var db: AppDb
    private val prefsName = "asi1_prefs"
    private var adapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)

        val toolbar = findViewById<Toolbar?>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_24)
        toolbar?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val etIncome = findViewById<EditText>(R.id.etIncome)
        val etExpLabel = findViewById<EditText>(R.id.etExpLabel)
        val etExpAmount = findViewById<EditText>(R.id.etExpAmount)
        val cbRecurring = findViewById<CheckBox>(R.id.cbRecurring)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val lvExpenses = findViewById<ListView>(R.id.lvExpenses)

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        etIncome.setText(prefs.getString("income", ""))

        db = AppDb(this)
        refreshList(lvExpenses)

        btnAddExpense.setOnClickListener {
            val label = etExpLabel.text.toString().trim()
            val amount = etExpAmount.text.toString().toDoubleOrNull() ?: 0.0
            val recurring = if (cbRecurring.isChecked) 1 else 0
            if (label.isNotEmpty() && amount > 0) {
                db.insertExpense(label, amount, recurring)
                etExpLabel.setText("")
                etExpAmount.setText("")
                cbRecurring.isChecked = true
                refreshList(lvExpenses)
            }
        }

        etIncome.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val e = prefs.edit()
                e.putString("income", etIncome.text.toString())
                e.apply()
            }
        }

        lvExpenses.setOnItemClickListener { _, _, id, _ ->
            db.deleteExpense(id.toLong())
            refreshList(listView = lvExpenses)
        }
    }

    private fun refreshList(listView: ListView) {
        val c: Cursor = db.getAllExpenses()
        if (adapter == null) {
            adapter = SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                c,
                arrayOf("label", "amount"),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                0
            )
            listView.adapter = adapter
        } else {
            adapter?.changeCursor(c)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.cursor?.close()
        db.close()
    }
}
