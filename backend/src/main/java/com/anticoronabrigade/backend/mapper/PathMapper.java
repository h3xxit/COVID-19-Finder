package com.anticoronabrigade.backend.mapper;

import com.anticoronabrigade.backend.dto.PathDto;
import com.anticoronabrigade.backend.dto.PointDto;
import com.anticoronabrigade.backend.entity.Path;
import com.anticoronabrigade.backend.entity.Point;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PathMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Point pointDtoToPoint(PointDto pointDto){
        return modelMapper.map(pointDto, Point.class);
    }

    public PointDto pointToPointDto(Point point) {
        return modelMapper.map(point, PointDto.class);
    }

    public Path pathDtoToPath(PathDto pathDto){
        Path path = modelMapper.map(pathDto, Path.class);
        path.setStartLongitude(pathDto.getPoints().get(0).getLongitude());
        path.setStartLatitude(pathDto.getPoints().get(0).getLatitude());
        return path;
    }
}
