package com.anticoronabrigade.backend.dto;

public class RegisterCodeDto {
    private Integer code;
    private Long date;

    public RegisterCodeDto(Integer code, Long date) {
        this.code = code;
        this.date = date;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
