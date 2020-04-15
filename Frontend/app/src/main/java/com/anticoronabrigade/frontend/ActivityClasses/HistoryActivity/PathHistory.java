package com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity;

public class PathHistory {
    private String location, firstText, secondText;
    private String beforeFirstText, beforeSecondText;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFirstText() {
        return firstText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getBeforeFirstText() {
        return beforeFirstText;
    }

    public void setBeforeFirstText(String beforeFirstText) {
        this.beforeFirstText = beforeFirstText;
    }

    public String getBeforeSecondText() {
        return beforeSecondText;
    }

    public void setBeforeSecondText(String beforeSecondText) {
        this.beforeSecondText = beforeSecondText;
    }

    public String getSecondText() {
        return secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public PathHistory(String location, String firstText, String secondText, String beforeFirstText, String beforeSecondText) {
        this.location = location;
        this.firstText = firstText;
        this.secondText = secondText;
        this.beforeFirstText = beforeFirstText;
        this.beforeSecondText = beforeSecondText;
    }
}
