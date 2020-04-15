package com.anticoronabrigade.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "path_point")
public class Path_point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "path_id")
    private Long path_id;

    @Column(name = "point_id")
    private Long point_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPath_id() {
        return path_id;
    }

    public void setPath_id(Long path_id) {
        this.path_id = path_id;
    }

    public Long getPoint_id() {
        return point_id;
    }

    public void setPoint_id(Long point_id) {
        this.point_id = point_id;
    }
}
