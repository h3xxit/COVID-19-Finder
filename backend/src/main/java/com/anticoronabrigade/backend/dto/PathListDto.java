package com.anticoronabrigade.backend.dto;

import java.util.List;

public class PathListDto {
    private List<PathDto> AllPaths;

    public List<PathDto> getAllPaths() {
        return AllPaths;
    }

    public void setAllPaths(List<PathDto> allPaths) {
        AllPaths = allPaths;
    }
}
