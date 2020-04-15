package com.anticoronabrigade.frontend.ActivityClasses.Main;

public class ChangePasswordDto {
    private String email;
    private String password;
    private Integer code;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(String email, String password, Integer code) {
        this.email = email;
        this.password = password;
        this.code = code;
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
