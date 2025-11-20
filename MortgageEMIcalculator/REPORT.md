# Mortgage EMI Calculator Report

## Overview
This Android app lets a user estimate a mortgage EMI, record income and expenses, and view a quick monthly summary. Screens are simple and rely on built-in SQLite for expenses plus shared preferences to remember recent input.

## Screens
- **Home navigation (MainActivity):** Three buttons open the EMI calculator, income/expense tracker, and summary.
- **EMI calculator (EmiActivity):** Accepts loan amount, annual interest, years, and months, then computes the monthly EMI. Inputs and the latest result are saved for convenience.
- **Income & expenses (IncomeActivity):** Stores monthly income in preferences. Lets the user add labeled expenses (recurring or variable) to a local SQLite table and tap an entry to delete it.
- **Summary (SummaryActivity):** Reads income and EMI from preferences and expense totals from the database, then shows recurring, variable, and total outflow plus whether the user has a monthly savings or deficit.

## Data storage
- **Shared preferences:** Hold the last EMI inputs/result and monthly income text so values persist between app launches.
- **SQLite table (AppDb):** Saves expense rows with label, amount, and recurring flag. Totals are computed directly with SQL grouping when the summary opens.

## Notable implementation details
- Toolbar back arrows on secondary screens for quick navigation.
- Simple layouts with Material 3 theme applied through `Theme.Asi1`.
- Short comments above functions highlight the purpose of each method.

## How requirements are met
- EMI calculation uses entered loan, rate, and term values and saves the result.
- Expenses can be added or removed, and recurring vs. variable spending is totaled.
- Summary displays income, EMI, total outflows, and the resulting surplus or deficit.
