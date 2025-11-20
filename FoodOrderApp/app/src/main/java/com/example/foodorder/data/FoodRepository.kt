package com.example.foodorder.data

class FoodRepository(private val dao: FoodDao) {
    // exposes live list of all menu items
    val items = dao.getAllItems()

    // adds a new food entry
    suspend fun addItem(name: String, cost: Double) {
        dao.insertItem(FoodItem(name = name, cost = cost))
    }

    // updates an existing food entry
    suspend fun updateItem(item: FoodItem) {
        dao.updateItem(item)
    }

    // deletes a food entry
    suspend fun deleteItem(item: FoodItem) {
        dao.deleteItem(item)
    }

    // saves plan and selections for a date
    suspend fun savePlan(date: String, target: Double, selected: List<FoodItem>) {
        dao.deletePlanByDate(date)
        val total = selected.sumOf { it.cost }
        val planId = dao.insertPlan(
            OrderPlan(planDate = date, targetCost = target, totalCost = total)
        )
        val selections = selected.map { PlanSelection(planId = planId, itemId = it.id) }
        dao.insertSelections(selections)
    }

    // loads a plan for a date
    suspend fun planForDate(date: String): PlanWithItems? = dao.getPlanForDate(date)
}
