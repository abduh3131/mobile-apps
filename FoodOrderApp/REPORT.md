# Food Order Planner Report

## Overview
This Android app helps a user plan daily meals without going over a spending target. A built-in Room database starts with 20
food items and prices. The user picks a date, sets a cost target, chooses items, and saves the plan. A lookup screen shows what
was saved for any date.

## Data layer (what stores the info)
- **Entities and DAO:** `FoodItem`, `OrderPlan`, and `PlanSelection` tables hold menu rows, saved plan headers, and the link
  rows between them. `PlanWithItems` stitches a plan together with its items to make display simple. The DAO functions add,
  update, delete, and query those records.
- **Database:** `FoodDatabase` builds the Room database and seeds 20 common foods the first time the app runs so the list is not
  empty. It exposes the DAO through a singleton for reuse across screens.
- **Repository:** `FoodRepository` is a small helper that exposes a live list of all items and wraps the DAO calls so UI code
  can add, edit, delete, save a plan, or fetch a plan for a date without touching SQL directly.

## UI layer (what the user taps)
- **FoodOrderActivity:** Main planner. The user picks a date with a date picker, types a target cost, and checks foods in a
  list. The app stops any pick that would push the total above the target and shows the running total. Buttons let the user save
  the plan, open the saved-plan lookup, or manage the item list.
- **ManageItemsActivity:** Inventory screen. A simple form adds a new item. Each row has edit and delete buttons; the edit button
  opens a small dialog where the user can change the name or cost.
- **PlanLookupActivity:** Query screen. The user picks a date with the date picker and taps lookup. The screen shows the plan
  date, target, total, and each item with its cost, or a "no plan" message if nothing was saved.

## Layouts and interaction pieces
- **RecyclerViews:** The order screen uses a checkbox list to pick foods. The manage screen uses a list with edit/delete buttons
  on each row. Both lists have dedicated adapters.
- **Dialogs:** A compact edit dialog keeps the manage screen clean while still letting the user update items.
- **Date pickers:** Both the planner and lookup screens share the same date picker pattern for consistency and fewer errors.

## How it meets the requirements
- Database includes at least 20 food items with costs (seeded in `FoodDatabase`).
- User can set a target cost per day, pick a date, and select food items without going over that target in `FoodOrderActivity`.
- Saving stores the chosen items (order plan) for that date in the database.
- `PlanLookupActivity` lets the user query and show a plan by date.
- `ManageItemsActivity` lets the user add, delete, or update entries in the menu list.
