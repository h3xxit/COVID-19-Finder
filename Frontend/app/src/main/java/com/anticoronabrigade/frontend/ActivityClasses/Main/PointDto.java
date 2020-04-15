package com.anticoronabrigade.frontend.ActivityClasses.Main;

public class PointDto {
    private Double latitude, longitude;

    public PointDto() {
    }

    public PointDto(PointDto pointDto){
        this.latitude=pointDto.getLatitude();
        this.longitude=pointDto.getLongitude();
    }

    public PointDto(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
