package com.example.foodorder.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface FoodDao {
    // gets all menu items in alphabetical order
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllItems(): LiveData<List<FoodItem>>

    // seeds the database with starter items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItem>)

    // inserts one item and returns its id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem): Long

    // updates a menu item
    @Update
    suspend fun updateItem(item: FoodItem)

    // removes a menu item
    @Delete
    suspend fun deleteItem(item: FoodItem)

    // inserts a plan header
    @Insert
    suspend fun insertPlan(plan: OrderPlan): Long

    // inserts selected items for a plan
    @Insert
    suspend fun insertSelections(selections: List<PlanSelection>)

    // clears any existing plan for a date
    @Query("DELETE FROM order_plans WHERE plan_date = :date")
    suspend fun deletePlanByDate(date: String)

    // loads a plan with its selections for a date
    @Transaction
    @Query("SELECT * FROM order_plans WHERE plan_date = :date LIMIT 1")
    suspend fun getPlanForDate(date: String): PlanWithItems?
}
