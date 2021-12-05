package com.trackiness.services.models;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class BalanceStatistic{
    private int id;
    private String label;
    private String total;
    private String Income;
    private String Expense;
    private ArrayList<Entry> IncomeData;
    private ArrayList<Entry> ExpenseData;

    public BalanceStatistic(int id, String label,String total, String income, String expense, ArrayList<Entry> incomeData, ArrayList<Entry> expenseData) {
        this.id = id;
        this.label = label;
        this.total = total;
        Income = income;
        Expense = expense;
        IncomeData = incomeData;
        ExpenseData = expenseData;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
    public String getTotal() {
        return total;
    }
    public String getIncome() {
        return Income;
    }
    public String getExpense() {
        return Expense;
    }
    public ArrayList<Entry> getIncomeData() {
        return IncomeData;
    }
    public ArrayList<Entry> getExpenseData() {
        return ExpenseData;
    }

}
