package com.example.asi1.data

class FoodRepository(private val dao: FoodDao) {
    val items = dao.getAllItems()

    suspend fun addItem(name: String, cost: Double) {
        dao.insertItem(FoodItem(name = name, cost = cost))
    }

    suspend fun updateItem(item: FoodItem) {
        dao.updateItem(item)
    }

    suspend fun deleteItem(item: FoodItem) {
        dao.deleteItem(item)
    }

    suspend fun savePlan(date: String, target: Double, selected: List<FoodItem>) {
        dao.deletePlanByDate(date)
        val total = selected.sumOf { it.cost }
        val planId = dao.insertPlan(
            OrderPlan(planDate = date, targetCost = target, totalCost = total)
        )
        val selections = selected.map { PlanSelection(planId = planId, itemId = it.id) }
        dao.insertSelections(selections)
    }

    suspend fun planForDate(date: String): PlanWithItems? = dao.getPlanForDate(date)
}
