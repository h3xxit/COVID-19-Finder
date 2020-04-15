package com.anticoronabrigade.frontend.ActivityClasses.Main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseDto {
    @SerializedName("is_infected")
    @Expose
    private Boolean isInfected;

    @SerializedName("start_infected_date")
    @Expose
    private Long startInfectedDate;

    public Boolean getInfected() {
        return isInfected;
    }

    public void setInfected(Boolean infected) {
        isInfected = infected;
    }

    public Long getStartInfectedDate() {
        return startInfectedDate;
    }

    public void setStartInfectedDate(Long startInfectedDate) {
        this.startInfectedDate = startInfectedDate;
    }
}
