package com.bing.tracedemo.ui.tracelist;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class Trace  {
    private String points;
    private String time;
    private String location;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Trace(String points, String time, String location) {
        this.points = points;
        this.time = time;
        this.location = location;
    }
}
