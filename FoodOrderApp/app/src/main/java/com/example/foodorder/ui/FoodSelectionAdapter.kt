package com.example.foodorder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.data.FoodItem
import com.example.foodorder.databinding.ItemFoodSelectionBinding

class FoodSelectionAdapter(
    private var items: List<FoodItem> = emptyList(),
    private val onToggle: (FoodItem, Boolean) -> Boolean
) : RecyclerView.Adapter<FoodSelectionAdapter.FoodViewHolder>() {

    private val selectedIds = mutableSetOf<Int>()

    // updates the list shown
    fun submitList(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    // returns currently checked items
    fun currentSelection(): List<FoodItem> = items.filter { selectedIds.contains(it.id) }

    // inflates each row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    // binds data to a row
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // returns row count
    override fun getItemCount(): Int = items.size

    inner class FoodViewHolder(private val binding: ItemFoodSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        // binds food info and checkbox behavior
        fun bind(item: FoodItem) {
            binding.foodName.text = item.name
            binding.foodCost.text = "${'$'}${String.format("%.2f", item.cost)}"
            binding.foodCheck.setOnCheckedChangeListener(null)
            binding.foodCheck.isChecked = selectedIds.contains(item.id)
            binding.foodCheck.setOnCheckedChangeListener { _, isChecked ->
                val accepted = onToggle(item, isChecked)
                if (accepted) {
                    if (isChecked) selectedIds.add(item.id) else selectedIds.remove(item.id)
                } else {
                    binding.foodCheck.isChecked = !isChecked
                }
            }
        }
    }
}
