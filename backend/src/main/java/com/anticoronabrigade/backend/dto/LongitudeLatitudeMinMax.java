package com.anticoronabrigade.backend.dto;

public class LongitudeLatitudeMinMax {

    private Double latitudeMin;
    private Double latitudeMax;
    private Double longitudeMin;
    private Double longitudeMax;

    public LongitudeLatitudeMinMax() {
    }

    public LongitudeLatitudeMinMax(Double longitudeMin, Double longitudeMax, Double latitudeMin, Double latitudeMax) {
        this.latitudeMin = latitudeMin;
        this.latitudeMax = latitudeMax;
        this.longitudeMin = longitudeMin;
        this.longitudeMax = longitudeMax;
    }

    public Double getLatitudeMin() {
        return latitudeMin;
    }

    public void setLatitudeMin(Double latitudeMin) {
        this.latitudeMin = latitudeMin;
    }

    public Double getLatitudeMax() {
        return latitudeMax;
    }

    public void setLatitudeMax(Double latitudeMax) {
        this.latitudeMax = latitudeMax;
    }

    public Double getLongitudeMin() {
        return longitudeMin;
    }

    public void setLongitudeMin(Double longitudeMin) {
        this.longitudeMin = longitudeMin;
    }

    public Double getLongitudeMax() {
        return longitudeMax;
    }

    public void setLongitudeMax(Double longitudeMax) {
        this.longitudeMax = longitudeMax;
    }
}
