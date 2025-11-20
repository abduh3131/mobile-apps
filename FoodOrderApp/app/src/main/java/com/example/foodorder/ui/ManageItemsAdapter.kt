package com.example.foodorder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.data.FoodItem
import com.example.foodorder.databinding.ItemManageFoodBinding

class ManageItemsAdapter(
    private var items: List<FoodItem> = emptyList(),
    private val onUpdate: (FoodItem) -> Unit,
    private val onDelete: (FoodItem) -> Unit
) : RecyclerView.Adapter<ManageItemsAdapter.ManageViewHolder>() {

    // updates the list shown
    fun submitList(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    // inflates each row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val binding = ItemManageFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageViewHolder(binding)
    }

    // binds data to a row
    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // returns row count
    override fun getItemCount(): Int = items.size

    inner class ManageViewHolder(private val binding: ItemManageFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        // binds food info and edit/delete actions
        fun bind(item: FoodItem) {
            binding.foodName.text = item.name
            binding.foodCost.text = "${'$'}${String.format("%.2f", item.cost)}"
            binding.editButton.setOnClickListener { onUpdate(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }
}
