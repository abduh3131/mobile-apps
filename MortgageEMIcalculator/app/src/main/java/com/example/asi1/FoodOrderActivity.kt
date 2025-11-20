package com.example.asi1

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asi1.data.FoodDatabase
import com.example.asi1.data.FoodItem
import com.example.asi1.data.FoodRepository
import com.example.asi1.databinding.ActivityFoodOrderBinding
import com.example.asi1.ui.FoodSelectionAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FoodOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodOrderBinding
    private lateinit var repository: FoodRepository
    private lateinit var adapter: FoodSelectionAdapter
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = FoodDatabase.getDatabase(this).foodDao()
        repository = FoodRepository(dao)

        adapter = FoodSelectionAdapter(onToggle = { item, checked -> handleSelection(item, checked) })

        binding.itemsRecycler.layoutManager = LinearLayoutManager(this)
        binding.itemsRecycler.adapter = adapter

        binding.dateValue.text = selectedDate
        binding.pickDateButton.setOnClickListener { openDatePicker() }
        binding.viewPlanButton.setOnClickListener { startActivity(PlanLookupActivity.intent(this)) }
        binding.manageItemsButton.setOnClickListener { startActivity(ManageItemsActivity.intent(this)) }
        binding.savePlanButton.setOnClickListener { savePlan() }

        repository.items.observe(this) { items ->
            adapter.submitList(items)
        }
    }

    private fun handleSelection(item: FoodItem, checked: Boolean): Boolean {
        val target = binding.targetInput.text.toString().toDoubleOrNull() ?: 0.0
        val current = adapter.currentSelection().sumOf { it.cost }
        val newTotal = if (checked) current + item.cost else current - item.cost
        return if (checked && newTotal > target && target > 0) {
            Toast.makeText(this, "Over target cost", Toast.LENGTH_SHORT).show()
            false
        } else {
            binding.totalValue.text = "${'$'}${String.format("%.2f", newTotal)}"
            true
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            binding.dateValue.text = selectedDate
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun savePlan() {
        val targetCost = binding.targetInput.text.toString().toDoubleOrNull()
        if (targetCost == null || targetCost <= 0) {
            Toast.makeText(this, "Enter target cost", Toast.LENGTH_SHORT).show()
            return
        }
        val selected = adapter.currentSelection()
        if (selected.isEmpty()) {
            Toast.makeText(this, "Pick at least one item", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            repository.savePlan(selectedDate, targetCost, selected)
            Toast.makeText(this@FoodOrderActivity, "Plan saved", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun intent(context: android.content.Context) =
            android.content.Intent(context, FoodOrderActivity::class.java)
    }
}
