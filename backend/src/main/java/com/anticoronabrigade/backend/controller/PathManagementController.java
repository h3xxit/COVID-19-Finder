package com.anticoronabrigade.backend.controller;

import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.dto.WithUserDto.PathListDtoWithUser;
import com.anticoronabrigade.backend.service.PathManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/path")
public class PathManagementController {

    @Autowired
    private PathManagementService pathManagementService;

    @PostMapping("/addPaths")
    @ResponseStatus(code = HttpStatus.CREATED)
    private @ResponseBody void addPath(@RequestBody PathListDtoWithUser pathList) {
        pathManagementService.addPath(pathList);
    }

    @PostMapping("/checkPath")
    @ResponseStatus(code = HttpStatus.OK)
    private MeetupLocationsDto checkPath(@RequestBody PathListDtoWithUser pathListDto){
        return pathManagementService.checkPath(pathListDto);
    }

    @GetMapping(value = "/getInfectedPaths/latitude={latitude}&&longitude={longitude}")
    @ResponseStatus(code = HttpStatus.OK)
    private List<PathDto> getInfectedPathsNewerThan5Days(@PathVariable Double latitude, @PathVariable Double longitude) {
        return pathManagementService.getAllPathsInRangeNewerThan5Days(new PointDto(latitude, longitude));
    }

    @PostMapping(value = "/uploadToTransfer")
    @ResponseStatus(code = HttpStatus.OK)
    private @ResponseBody void uploadDataToTransfer(@RequestBody TransferDto transferDto) {
       // throw new NotImplementedException();

    }

    @PostMapping(value = "/downloadToTransfer")
    @ResponseStatus(code = HttpStatus.OK)
    private TransferDto downloadDataToTransfer(@RequestBody TransferDto transferDto) {
        return null;
    }
}
