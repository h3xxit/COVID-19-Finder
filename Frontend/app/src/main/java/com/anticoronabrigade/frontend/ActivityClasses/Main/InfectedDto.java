package com.anticoronabrigade.frontend.ActivityClasses.Main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfectedDto {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("infected")
    @Expose
    private boolean infected;

    public InfectedDto() {
    }

    public InfectedDto(String email, boolean infected) {
        this.email = email;
        this.infected = infected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
