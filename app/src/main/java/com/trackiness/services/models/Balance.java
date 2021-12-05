package com.trackiness.services.models;

import java.util.Date;

public class Balance {
    Long id;
    String currency;
    float amount;
    String label;
    Date created_at;
    Date updated_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
}
