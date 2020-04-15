package com.anticoronabrigade.backend.dto.WithUserDto;

import com.anticoronabrigade.backend.dto.PathDto;

import java.util.List;

public class PathListDtoWithUser {

    //private String email;
    //private String password;
    private List<PathDto> AllPaths;

    /*public String getEmail() {
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
    }*/

    public List<PathDto> getAllPaths() {
        return AllPaths;
    }

    public void setAllPaths(List<PathDto> allPaths) {
        AllPaths = allPaths;
    }

}
