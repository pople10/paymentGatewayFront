package com.trackiness.services.models;

import com.trackiness.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentConfirmation {
    Date dateTransfer;
    String idTransfer;
    String sender;
    String receiver;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    float amount;
    String currency;

    public Date getDateTransfer() {
        return dateTransfer;
    }

    public void setDateTransfer(Date dateTransfer) {
        this.dateTransfer = dateTransfer;
    }

    public String getIdTransfer() {
        return idTransfer;
    }

    public void setIdTransfer(String idTransfer) {
        this.idTransfer = idTransfer;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDateString()
    {
        return this.dateTransfer.toString();
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

    public String generateDate()
    {
        return Utility.generateDate(dateTransfer);
    }
}
