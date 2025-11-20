package com.example.asi1

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.asi1.data.FoodDatabase
import com.example.asi1.data.FoodRepository
import com.example.asi1.databinding.ActivityPlanLookupBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlanLookupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanLookupBinding
    private lateinit var repository: FoodRepository
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = FoodDatabase.getDatabase(this).foodDao()
        repository = FoodRepository(dao)

        binding.dateValue.text = selectedDate
        binding.pickDateButton.setOnClickListener { openDatePicker() }
        binding.lookupButton.setOnClickListener { loadPlan() }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            binding.dateValue.text = selectedDate
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadPlan() {
        lifecycleScope.launch {
            val plan = repository.planForDate(selectedDate)
            if (plan == null) {
                binding.planText.text = getString(R.string.no_plan)
            } else {
                val itemLines = plan.items.joinToString("\n") { "- ${'$'}${String.format("%.2f", it.cost)} ${it.name}" }
                binding.planText.text = buildString {
                    appendLine("${getString(R.string.plan_for)} ${plan.plan.planDate}")
                    appendLine("Target: ${'$'}${String.format("%.2f", plan.plan.targetCost)}")
                    appendLine("Total: ${'$'}${String.format("%.2f", plan.plan.totalCost)}")
                    append("Items:\n$itemLines")
                }
            }
        }
    }

    companion object {
        fun intent(context: Context) = android.content.Intent(context, PlanLookupActivity::class.java)
    }
}
