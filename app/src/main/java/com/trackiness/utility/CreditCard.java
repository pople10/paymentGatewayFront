package com.trackiness.utility;

public class CreditCard {

    private String name;
    private String cvv;
    private String number;
    private String expDate;

    public CreditCard(String name, String cvv, String number, String expDate) {
        this.name = name;
        this.cvv = cvv;
        this.number = number;
        this.expDate = expDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
