package com.trackiness.services.models;

import com.trackiness.utility.Utility;

import java.math.BigDecimal;
import java.util.Date;

public class Wallet {
    String wallet;
    double amountHeld;
    String active;
    Date created_at;
    Date updated_at;

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public double getAmountHeld() {
        return amountHeld;
    }

    public void setAmountHeld(double amountHeld) {
        this.amountHeld = amountHeld;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String generateLabel()
    {
        String out ="";
        out+="Amount held : "+ BigDecimal.valueOf(amountHeld).setScale(18, BigDecimal.ROUND_HALF_EVEN)+"\n\n";
        out+="Status : "+(active.equals("1")?"Active":"Closed")+"\n\n";
        out+="Last time update : "+ Utility.generateDate(updated_at);
        return out;
    }
    public boolean isActive()
    {
        return active.equals("1");
    }
}
