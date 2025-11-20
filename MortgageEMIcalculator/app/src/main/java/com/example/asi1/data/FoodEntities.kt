package com.example.asi1.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val cost: Double
)

@Entity(
    tableName = "order_plans"
)
data class OrderPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "plan_date") val planDate: String,
    @ColumnInfo(name = "target_cost") val targetCost: Double,
    @ColumnInfo(name = "total_cost") val totalCost: Double
)

@Entity(
    tableName = "plan_selections",
    primaryKeys = ["planId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlanSelection(
    val planId: Long,
    val itemId: Int
)

data class PlanWithItems(
    @Embedded val plan: OrderPlan,
    @Relation(parentColumn = "id", entityColumn = "planId", entity = PlanSelection::class)
    val selections: List<PlanSelection> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlanSelection::class,
            parentColumn = "planId",
            entityColumn = "itemId"
        )
    )
    val items: List<FoodItem> = emptyList()
)
