package com.egco428.logintest;

/**
 * Created by dell pc on 14/12/2559.
 */
public class DataFirebase {
    private String usernamefb;
    private double lat;
    private double lon;

    public DataFirebase(){

    }

    public DataFirebase(String username, double lat, double lon) {
        this.usernamefb = username;
        this.lat = lat;
        this.lon = lon;
    }

    public String getUsernameFb() {
        return usernamefb;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
