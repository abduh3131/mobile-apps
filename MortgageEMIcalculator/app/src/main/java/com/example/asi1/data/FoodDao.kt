package com.example.asi1.data

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
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllItems(): LiveData<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem): Long

    @Update
    suspend fun updateItem(item: FoodItem)

    @Delete
    suspend fun deleteItem(item: FoodItem)

    @Insert
    suspend fun insertPlan(plan: OrderPlan): Long

    @Insert
    suspend fun insertSelections(selections: List<PlanSelection>)

    @Query("DELETE FROM order_plans WHERE plan_date = :date")
    suspend fun deletePlanByDate(date: String)

    @Transaction
    @Query("SELECT * FROM order_plans WHERE plan_date = :date LIMIT 1")
    suspend fun getPlanForDate(date: String): PlanWithItems?
}
