package com.example.admin.stocked;

public class User {
    public String email,name,surname,currency;

    public User(){

    }

    public User(String currency, String email, String name, String surname) {
        this.currency = currency;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
