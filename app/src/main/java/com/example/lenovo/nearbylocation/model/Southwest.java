package com.example.lenovo.nearbylocation.model;

/**
 * Created by LENOVO on 29/05/2018.
 */

public class Southwest {
    private String lng;

    private String lat;

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [lng = "+lng+", lat = "+lat+"]";
    }
}
