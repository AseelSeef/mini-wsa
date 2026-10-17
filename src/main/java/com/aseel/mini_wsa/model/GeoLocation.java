package com.aseel.mini_wsa.model;

public class GeoLocation {

    private String country;
    private String city;

    public GeoLocation() {
    }

    public GeoLocation(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
}
