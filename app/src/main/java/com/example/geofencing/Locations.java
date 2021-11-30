package com.example.geofencing;

import com.google.android.gms.maps.model.LatLng;

public class Locations {
    //Latlng is a java class it hold or it's paramaters is lat and long so i didn't diclare it again,
    private LatLng latlng;
    private String city;

    public Locations() {

    }

    public Locations(LatLng latlng, Double lng, String city) {
        this.latlng=latlng;
        this.city = city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getCity() {
        return city;
    }
}
