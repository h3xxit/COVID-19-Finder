package com.anticoronabrigade.frontend.ActivityClasses.Main;

import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PathListDto {
    @SerializedName("allPaths")
    @Expose
    private List<WalkedPath> AllPaths;

    /*@SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

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
    }*/

    public List<WalkedPath> getAllPaths() {
        return AllPaths;
    }

    public void setAllPaths(List<WalkedPath> allPaths) {
        AllPaths = allPaths;
    }
}
