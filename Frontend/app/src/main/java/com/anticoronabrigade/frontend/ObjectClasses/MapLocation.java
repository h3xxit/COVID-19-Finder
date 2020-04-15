package com.anticoronabrigade.frontend.ObjectClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapLocation {
    @SerializedName("latitude")
    @Expose
    public Double latitude;
    @SerializedName("longitude")
    @Expose
    public Double longitude;

    public MapLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }

    public String stringSeparatedByEnter() {
        return latitude + "\n" + longitude;
    }
}
