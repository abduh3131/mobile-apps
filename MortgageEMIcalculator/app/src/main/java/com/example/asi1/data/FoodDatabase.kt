package com.example.asi1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FoodItem::class, OrderPlan::class, PlanSelection::class], version = 1, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_order_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seedDatabase(context.applicationContext)
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun seedDatabase(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).foodDao()
                val defaults = listOf(
                    FoodItem(name = "Grilled Chicken", cost = 8.5),
                    FoodItem(name = "Veggie Wrap", cost = 6.0),
                    FoodItem(name = "Beef Burger", cost = 9.0),
                    FoodItem(name = "Caesar Salad", cost = 5.5),
                    FoodItem(name = "Pasta Alfredo", cost = 7.75),
                    FoodItem(name = "Tomato Soup", cost = 4.0),
                    FoodItem(name = "Fruit Bowl", cost = 3.25),
                    FoodItem(name = "Yogurt Parfait", cost = 3.5),
                    FoodItem(name = "Avocado Toast", cost = 5.0),
                    FoodItem(name = "Breakfast Burrito", cost = 6.25),
                    FoodItem(name = "Sushi Roll", cost = 10.0),
                    FoodItem(name = "Chicken Tacos", cost = 7.0),
                    FoodItem(name = "Fish and Chips", cost = 8.0),
                    FoodItem(name = "Veggie Pizza", cost = 9.5),
                    FoodItem(name = "BBQ Ribs", cost = 12.0),
                    FoodItem(name = "Steak and Fries", cost = 13.5),
                    FoodItem(name = "Falafel Bowl", cost = 7.0),
                    FoodItem(name = "Hummus Plate", cost = 4.5),
                    FoodItem(name = "Chocolate Cake", cost = 3.75),
                    FoodItem(name = "Iced Coffee", cost = 2.5)
                )
                dao.insertItems(defaults)
            }
        }
    }
}
