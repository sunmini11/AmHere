package com.egco428.logintest;

/**
 * Created by pam on 12/12/2016.
 */

//Get data from login table
public class LoginMessage {
    private long id;
    private String username;
    private String password;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return username;
    }
}
