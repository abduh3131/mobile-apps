package com.example.foodorder

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorder.data.FoodDatabase
import com.example.foodorder.data.FoodItem
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.databinding.ActivityManageItemsBinding
import com.example.foodorder.databinding.DialogEditItemBinding
import com.example.foodorder.ui.ManageItemsAdapter
import kotlinx.coroutines.launch

class ManageItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageItemsBinding
    private lateinit var repository: FoodRepository
    private lateinit var adapter: ManageItemsAdapter

    // sets up the manage items screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = FoodDatabase.getDatabase(this).foodDao()
        repository = FoodRepository(dao)

        adapter = ManageItemsAdapter(onUpdate = { item -> showUpdateDialog(item) }, onDelete = { item -> deleteItem(item) })
        binding.itemsRecycler.layoutManager = LinearLayoutManager(this)
        binding.itemsRecycler.adapter = adapter

        repository.items.observe(this) { items -> adapter.submitList(items) }

        binding.addButton.setOnClickListener { addItem() }
    }

    // adds a new menu entry
    private fun addItem() {
        val name = binding.nameInput.text.toString()
        val cost = binding.costInput.text.toString().toDoubleOrNull()
        if (name.isBlank() || cost == null) {
            Toast.makeText(this, "Enter name and cost", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            repository.addItem(name, cost)
            binding.nameInput.text?.clear()
            binding.costInput.text?.clear()
        }
    }

    // shows dialog to edit item
    private fun showUpdateDialog(item: FoodItem) {
        val dialogBinding = DialogEditItemBinding.inflate(layoutInflater)
        dialogBinding.nameInput.setText(item.name)
        dialogBinding.costInput.setText(item.cost.toString())
        AlertDialog.Builder(this)
            .setTitle(R.string.update_item)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.update_item) { dialog, _ ->
                val newName = dialogBinding.nameInput.text.toString()
                val newCost = dialogBinding.costInput.text.toString().toDoubleOrNull()
                if (newName.isBlank() || newCost == null) {
                    Toast.makeText(this, "Enter valid data", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch { repository.updateItem(item.copy(name = newName, cost = newCost)) }
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    // deletes an item
    private fun deleteItem(item: FoodItem) {
        lifecycleScope.launch { repository.deleteItem(item) }
    }

    companion object {
        // builds an intent to open this screen
        fun intent(context: Context) = android.content.Intent(context, ManageItemsActivity::class.java)
    }
}
