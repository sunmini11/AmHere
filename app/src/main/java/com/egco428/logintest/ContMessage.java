package com.egco428.logintest;

/**
 * Created by pam on 12/12/2016.
 */
//Get data from contact table
public class ContMessage {
    private long id;
    private String username;
    private String number;
    private String name;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return username;
    }
}
