package com.anticoronabrigade.frontend.ActivityClasses.Main;

public class MeetupPointDto {
    private PointDto point;
    private Long date;

    public MeetupPointDto(PointDto point, Long date) {
        this.point = point;
        this.date = date;
    }

    public PointDto getPoint() {
        return point;
    }

    public void setPoint(PointDto point) {
        this.point = point;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
