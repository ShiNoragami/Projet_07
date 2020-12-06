package com.sn.go4lunch.Model.PlaceInfo.PlaceDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Open {
    @Expose
    public Integer day;
    @SerializedName("time")
    @Expose
    public String time;

    public Integer getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
