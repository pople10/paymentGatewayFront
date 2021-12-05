package com.trackiness.services.models;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionData {
    String idPayment;
    Long from;
    Long to;
    String currency;
    Double amount;
    int typeT;
    String name;
    Date date;
    String balance;
    int status;
    String type;
    String fullname;
    String title;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTypeT() {
        return typeT;
    }

    public void setTypeT(int typeT) {
        this.typeT = typeT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public String generateLabel()
    {
        String preffix="";
        if(amount<0)
            preffix="- ";
        if(currency.equals("USD"))
            return preffix+"$"+amount;
        if(currency.equals("EUR"))
            return preffix+"€"+amount;
        if(currency.equals("GBP"))
            return preffix+"£"+amount;
        return preffix+amount+" "+currency;
    }
     public String getStringDate(){
         DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
         return dateFormat.format(date);
     }
}
