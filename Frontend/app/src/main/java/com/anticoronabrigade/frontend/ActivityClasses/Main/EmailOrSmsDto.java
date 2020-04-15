package com.anticoronabrigade.frontend.ActivityClasses.Main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmailOrSmsDto {
    @SerializedName("emailOrPhone")
    @Expose
    private String emailOrSms;

    public EmailOrSmsDto() {
    }

    public EmailOrSmsDto(String emailOrSms) {
        this.emailOrSms = emailOrSms;
    }

    public String getEmailOrSms() {
        return emailOrSms;
    }

    public void setEmailOrSms(String emailOrSms) {
        this.emailOrSms = emailOrSms;
    }
}
