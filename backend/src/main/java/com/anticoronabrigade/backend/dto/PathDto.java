package com.anticoronabrigade.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class PathDto {
    private Long date;
    private List<PointDto> points;

    public PathDto()
    {
        date = 0L;
        points = new ArrayList<>();
    }

    public PathDto(Long date, List<PointDto> points)
    {
        this.date = date;
        this.points = points;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<PointDto> getPoints() {
        return points;
    }

    public void setPoints(List<PointDto> points) {
        this.points = points;
    }
}
