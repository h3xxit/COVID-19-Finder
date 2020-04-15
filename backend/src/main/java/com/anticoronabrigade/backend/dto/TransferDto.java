package com.anticoronabrigade.backend.dto;

import java.util.List;

public class TransferDto {
    private Boolean isInfected;
    private List<PathDto> AllPaths;

    public Boolean getInfected() {
        return isInfected;
    }

    public void setInfected(Boolean infected) {
        isInfected = infected;
    }

    public List<PathDto> getAllPaths() {
        return AllPaths;
    }

    public void setAllPaths(List<PathDto> allPaths) {
        AllPaths = allPaths;
    }
}
