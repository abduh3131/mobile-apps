# Food Order Planner Report

## Overview
This Android app lets a user pick daily meals while staying under a cost target. A built-in Room database ships with 20 starter food items and supports adding, updating, or deleting entries. Users select a date, set a target cost per day, choose items without passing the target, and save the plan. They can later look up plans by date.

## Data layer
- **Entities and DAO:** `FoodItem`, `OrderPlan`, and `PlanSelection` tables keep menu items, saved plans, and the item links. `PlanWithItems` joins a plan with its selections for easy display. The DAO provides item CRUD operations plus plan insert/query helpers.
- **Database:** `FoodDatabase` is a Room database that seeds 20 common foods with prices on first launch. This keeps the list populated even before the user adds items.
- **Repository:** `FoodRepository` wraps the DAO to expose LiveData of all items, handles add/update/delete, and saves plans with their selected items while computing totals.

## UI layer
- **FoodOrderActivity:** Main screen for picking a date, entering a target cost, browsing the menu, and selecting foods. A check against the target warns when the selection would exceed the limit. The screen shows the running total and buttons to save the plan, view a saved plan, or manage menu items.
- **ManageItemsActivity:** Screen to add new menu items and edit or delete existing ones. A small dialog handles updates so the list stays organized.
- **PlanLookupActivity:** Date-based query screen. Users pick a date and see the stored plan with target, total, and itemized lines if it exists.

## Layouts and interaction
- **RecyclerViews:** Two adapters display selectable items and manageable items. The selectable list uses checkboxes; the manage list exposes edit/delete buttons.
- **Dialogs:** A simple edit dialog lets users adjust an item name or cost.
- **Date pickers:** Both plan and lookup screens use the Android date picker for consistency.

## How it meets the requirements
- Database includes at least 20 food items with costs (seeded in `FoodDatabase`).
- Users set a target cost, choose a date, and pick items without exceeding the target in `FoodOrderActivity`.
- Saving stores the plan and selected items to the database with the chosen date.
- `PlanLookupActivity` queries and shows a plan for a picked date.
- Add, delete, and update features are provided in `ManageItemsActivity`.

