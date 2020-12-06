package com.sn.go4lunch.Model.PlaceInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapPlacesInfos {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}