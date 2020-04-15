package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.content.Intent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDto {
    private String email;

    private String password;

    private Integer isInfected;

    public UserDto() {
    }

    public UserDto(String email, String password, Integer isInfected) {
        this.email = email;
        this.password = password;
        this.isInfected = isInfected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsInfected() {
        return isInfected;
    }

    public void setIsInfected(Integer isInfected) {
        this.isInfected = isInfected;
    }
}
