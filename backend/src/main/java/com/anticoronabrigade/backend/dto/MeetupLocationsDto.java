package com.anticoronabrigade.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class MeetupLocationsDto {
    private List<MeetupPointDto> points;

    public MeetupLocationsDto() {
        this.points = new ArrayList<>();
    }

    public void add(MeetupLocationsDto other)
    {
        points.addAll(other.getPoints());
    }

    public List<MeetupPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<MeetupPointDto> points) {
        this.points = points;
    }
}
