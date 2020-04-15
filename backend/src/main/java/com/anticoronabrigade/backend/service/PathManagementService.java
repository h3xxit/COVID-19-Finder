package com.anticoronabrigade.backend.service;

import com.anticoronabrigade.backend.customClasses.Constants;
import com.anticoronabrigade.backend.customClasses.PathIntersector;
import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.dto.WithUserDto.PathListDtoWithUser;
import com.anticoronabrigade.backend.entity.Path;
import com.anticoronabrigade.backend.entity.Path_point;
import com.anticoronabrigade.backend.entity.Point;
import com.anticoronabrigade.backend.entity.User;
import com.anticoronabrigade.backend.exception.NullValueInNotNullColumnException;
import com.anticoronabrigade.backend.exception.UnauthorizedException;
import com.anticoronabrigade.backend.exception.UserNotFoundException;
import com.anticoronabrigade.backend.mapper.PathMapper;
import com.anticoronabrigade.backend.repository.PathManagementRepository;
import com.anticoronabrigade.backend.repository.PointRepository;
import com.anticoronabrigade.backend.repository.PathPointRepository;
import com.anticoronabrigade.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Configurable
@Service
public class PathManagementService {



    @Autowired
    private PathMapper pathMapper;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PathManagementRepository pathManagementRepository;

    @Autowired
    private PathPointRepository pathPointRepository;

    @Autowired
    private PathIntersector pathIntersector;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private void uploadDataToTransfer(TransferDto transferDto) {
        deletePathsThatAreOlderThan10Days();

        List<PathDto> paths = transferDto.getAllPaths();
        if(paths==null)
            throw new NullValueInNotNullColumnException();
        for (PathDto path : paths) {
            if(path.getDate()==null)
                throw new NullValueInNotNullColumnException();
            List<PointDto> points = path.getPoints();
            if(points==null)
                throw new NullValueInNotNullColumnException();
            for (PointDto pointDto : points) {
                if(pointDto.getLatitude()==null || pointDto.getLongitude()==null) {
                    throw new NullValueInNotNullColumnException();
                }
            }
        }

        for(PathDto pathDto : transferDto.getAllPaths()) {
            //save path to it's table
            Path newPath = savePathToDatabase(pathMapper.pathDtoToPath(pathDto));
            for(PointDto pointDto : pathDto.getPoints()) {
                //save point to it's table
                Point newPoint = savePointToDatabase(pathMapper.pointDtoToPoint(pointDto));

                //save pathid with pointid for the path to a aux table
                Path_point pathPoint = new Path_point();
                pathPoint.setPath_id(newPath.getId());
                pathPoint.setPoint_id(newPoint.getId());
                savePathPointToDatabase(pathPoint);
            }
        }
    }

    public List<PathDto> getAllPathsInRangeNewerThan5Days(PointDto pointWhereYouAre) {
        List<PathDto> pathDtos = new ArrayList<>();
        LongitudeLatitudeMinMax coordinates = pathIntersector.getCoordinatesInRange(pointWhereYouAre);
        Long currentTimeInMillis = Calendar.getInstance().getTimeInMillis()-Constants.FIVE_DAYS_IN_MILLISECONDS;

        List<Path> paths = pathManagementRepository.getAllPathsThatStartsInRangeFromLast5Days(coordinates.getLongitudeMin(),
                                                                                              coordinates.getLongitudeMax(),
                                                                                              coordinates.getLatitudeMin(),
                                                                                              coordinates.getLatitudeMax(),
                                                                                              currentTimeInMillis);
        for(Path path : paths) {
            Long pathId = path.getId();
            Long date = path.getDate();
            List<Long> pointIds = pathPointRepository.findAllByPathId(pathId);
            List<PointDto> points = new ArrayList<>();
            for (Long pointId : pointIds) {
                Optional<Point> point = pointRepository.findById(pointId);
                point.ifPresent(value -> points.add(new PointDto(value.getLatitude(), value.getLongitude())));
            }
            pathDtos.add(new PathDto(date, points));
        }

        return pathDtos;
    }

    private void deletePathsThatAreOlderThan10Days(){
        Long currentTimeInMillis = Calendar.getInstance().getTimeInMillis()-Constants.TEN_DAYS_IN_MILLISECONDS;

        List<Path> paths=pathManagementRepository.findAllPathsThatAreOlderThan10Days(currentTimeInMillis);
        pathManagementRepository.deleteAllPathsThatAreOlderThan10Days(currentTimeInMillis);
        for (Path path : paths) {
            Long pathId=path.getId();
            List<Long> pointIds = pathPointRepository.findAllByPathId(pathId);
            pathPointRepository.deleteAllPointsPathByPathId(pathId);
            for (Long pointId : pointIds) {
                pointRepository.deleteById(pointId);
            }
        }
    }

    public void addPath(PathListDtoWithUser pathList) {
        deletePathsThatAreOlderThan10Days();

        /*if(pathList.getEmail()==null || pathList.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        User user = userRepository.findUserByEmailOrPhoneNumber(pathList.getEmail());

        if(user==null)
            throw new UnauthorizedException();

        if(!passwordEncoder.matches(pathList.getPassword(), user.getPassword()))
            throw new UnauthorizedException();*/

        List<PathDto> paths = pathList.getAllPaths();
        if(paths==null)
            throw new NullValueInNotNullColumnException();
        for (PathDto path : paths) {
            if(path.getDate()==null)
                throw new NullValueInNotNullColumnException();
            List<PointDto> points = path.getPoints();
            if(points==null)
                throw new NullValueInNotNullColumnException();
            for (PointDto pointDto : points) {
                if(pointDto.getLatitude()==null || pointDto.getLongitude()==null) {
                    throw new NullValueInNotNullColumnException();
                }
            }
        }

        for(PathDto pathDto : pathList.getAllPaths()) {
            //save path to it's table
            Path newPath = savePathToDatabase(pathMapper.pathDtoToPath(pathDto));
            for(PointDto pointDto : pathDto.getPoints()) {
                //save point to it's table
                Point newPoint = savePointToDatabase(pathMapper.pointDtoToPoint(pointDto));

                //save pathid with pointid for the path to a aux table
                Path_point pathPoint = new Path_point();
                pathPoint.setPath_id(newPath.getId());
                pathPoint.setPoint_id(newPoint.getId());
                savePathPointToDatabase(pathPoint);
            }
        }
    }

    private Point savePointToDatabase(Point point) {
        return pointRepository.save(point);
    }

    private Path savePathToDatabase(Path path) {
        return pathManagementRepository.save(path);
    }

    private void savePathPointToDatabase(Path_point pathPoint) {
        pathPointRepository.save(pathPoint);
    }

    public MeetupLocationsDto checkPath(PathListDtoWithUser pathList) {
        /*if(pathList.getEmail()==null || pathList.getPassword()==null)
            throw new NullValueInNotNullColumnException();

        User user = userRepository.findUserByEmailOrPhoneNumber(pathList.getEmail());

        if(user==null)
            throw new UnauthorizedException();

        if(!passwordEncoder.matches(pathList.getPassword(), user.getPassword()))
            throw new UnauthorizedException();*/

        return pathIntersector.checkPaths(pathList);
    }

}
