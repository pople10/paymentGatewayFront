package com.trackiness.services.models;

public class CreditCard {

    private String name;
    private String cvv;
    private String number;
    private String expiringDate;

    public CreditCard(String name, String cvv, String number, String expDate) {
        this.name = name;
        this.cvv = cvv;
        this.number = number;
        this.expiringDate = expDate;
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
        return getNumberSeperated();
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpDate() {
        return expiringDate;
    }

    public void setExpDate(String expDate) {
        this.expiringDate = expDate;
    }

    public String getNumberSeperated()
    {
        String out="";
        for(int i=0;i<number.length();i++)
        {
            if(i%4==0&&i!=0)
                out+=" ";
            out+=""+number.charAt(i);
        }
        return out;
    }

    public String getNormalNumber(){return number;}
}
