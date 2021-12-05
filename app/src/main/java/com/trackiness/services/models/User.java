package com.trackiness.services.models;

public class User {
    Long idUser;
    String firstname;
    String lastname;
    String username;
    String phone;
    String title;
    String cin;
    String company;
    String photoUrl;
    String email;
    String birth;
    String password;
    String accType;
    String mac;
    String enabledNotification;

    public Long getIdUser() {
        return idUser;
    }

    public User(String firstname, String lastname, String username, String phone, String title, String cin, String company, String photoUrl, String email, String birth, String password, String accType, String mac, String enabledNotification) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.phone = phone;
        this.title = title;
        this.cin = cin;
        this.company = company;
        this.photoUrl = photoUrl;
        this.email = email;
        this.birth = birth;
        this.password = password;
        this.accType = accType;
        this.mac = mac;
        this.enabledNotification = enabledNotification;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }


    public String getPhone() {
        return phone;
    }

    public String getTitle() {
        return title;
    }

    public String getCin() {
        return cin;
    }

    public String getCompany() {
        return company;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getBirth() {
        return birth;
    }

    public String getPassword() {
        return password;
    }

    public String getAccType() {
        return accType;
    }

    public String getMac() {
        return mac;
    }

    public String getEnabledNotification() {
        return enabledNotification;
    }
}
