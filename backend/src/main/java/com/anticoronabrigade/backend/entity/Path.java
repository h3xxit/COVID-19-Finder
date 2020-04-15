package com.anticoronabrigade.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "path")
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private Long date;

    @Column(name = "start_longitude")
    private Double startLongitude;

    @Column(name = "start_latitude")
    private Double startLatitude;

    //@Column(name = "code_for_transfer")
    //private Integer codeForTransfer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    /*public Integer getCodeForTransfer() {
        return codeForTransfer;
    }

    public void setCodeForTransfer(Integer codeForTransfer) {
        this.codeForTransfer = codeForTransfer;
    }*/
}
