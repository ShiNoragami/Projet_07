package com.sn.go4lunch.Model.PlaceInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sn.go4lunch.Model.PlaceInfo.PlaceDetail.Period;


import java.util.List;

public class OpeningHours {
    @SerializedName("open_now")
    @Expose
    public Boolean openNow;
    @Expose
    public List<Period> periods = null;
    @SerializedName("weekday_text")
    @Expose
    public List<String> weekdayText = null;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }
}