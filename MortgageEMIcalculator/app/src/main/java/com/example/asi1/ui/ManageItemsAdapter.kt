package com.example.asi1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.asi1.data.FoodItem
import com.example.asi1.databinding.ItemManageFoodBinding

class ManageItemsAdapter(
    private var items: List<FoodItem> = emptyList(),
    private val onUpdate: (FoodItem) -> Unit,
    private val onDelete: (FoodItem) -> Unit
) : RecyclerView.Adapter<ManageItemsAdapter.ManageViewHolder>() {

    fun submitList(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val binding = ItemManageFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ManageViewHolder(private val binding: ItemManageFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodItem) {
            binding.foodName.text = item.name
            binding.foodCost.text = "${'$'}${String.format("%.2f", item.cost)}"
            binding.editButton.setOnClickListener { onUpdate(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }
}
