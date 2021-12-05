package com.trackiness.services.models;

import java.util.ArrayList;
import java.util.List;

public class StatisticData {
    Balance balance;
    Double income;
    Double expense;
    List<Double> expenseData;
    List<Double> incomeData;

    public Balance getBalance() {
        return balance;
    }

    public Double getIncome() {
        return income;
    }

    public Double getExpense() {
        return expense;
    }

    public List<Double> getExpenseData() {
        return expenseData;
    }

    public List<Double> getIncomeData() {
        return incomeData;
    }

    @Override
    public String toString() {
        return "StatisticData{" +
                "balance=" + balance.toString() +
                ", income=" + income +
                ", expense=" + expense +
                ", expenseData=" + expenseData.toString() +
                ", incomeData=" + incomeData.toString() +
                '}';
    }
}
