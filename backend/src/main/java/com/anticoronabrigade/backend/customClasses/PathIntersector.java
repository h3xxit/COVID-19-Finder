package com.anticoronabrigade.backend.customClasses;

import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.dto.WithUserDto.PathListDtoWithUser;
import com.anticoronabrigade.backend.entity.Path;
import com.anticoronabrigade.backend.entity.Point;
import com.anticoronabrigade.backend.mapper.PathMapper;
import com.anticoronabrigade.backend.repository.PathManagementRepository;
import com.anticoronabrigade.backend.repository.PathPointRepository;
import com.anticoronabrigade.backend.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PathIntersector {



    @Autowired
    PathManagementRepository pathManagementRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    PathPointRepository pathPointRepository;

    @Autowired
    PathMapper pathMapper;

    private Calendar getCalendar(String date) {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            return new Calendar.Builder().setInstant(dateFormatter.parse(date)).build();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public LongitudeLatitudeMinMax getCoordinatesInRange(PointDto startPoint){
        Double longitudeToAdd = 10/110.574;
        Double latitudeToAdd = 10/(111.32*Math.cos(startPoint.getLatitude()));

        Double longitudeMin = startPoint.getLongitude()-longitudeToAdd;
        Double longitudeMax = startPoint.getLongitude()+longitudeToAdd;
        Double latitudeMin = startPoint.getLatitude()-latitudeToAdd;
        Double latitudeMax = startPoint.getLatitude()+latitudeToAdd;

        if(longitudeMin>longitudeMax){
            Double aux=longitudeMin;
            longitudeMin=longitudeMax;
            longitudeMax=aux;
        }

        if(latitudeMin>latitudeMax){
            Double aux=latitudeMin;
            latitudeMin=latitudeMax;
            latitudeMax=aux;
        }

        return new LongitudeLatitudeMinMax(longitudeMin, longitudeMax, latitudeMin, latitudeMax);
    }

    public MeetupLocationsDto checkPaths(PathListDtoWithUser me) {
        MeetupLocationsDto meetupLocationsDto = new MeetupLocationsDto();
        List<List<PathDto> > currentList = new ArrayList<>();
        for(PathDto path : me.getAllPaths()) {
            boolean shouldIAddANewLocation = true;
            for(List<PathDto> addedList : currentList) {
                if(measure(addedList.get(0).getPoints().get(0), path.getPoints().get(0)) <= Constants.MIN_DIST_TO_CONSIDER_PATH) {
                    shouldIAddANewLocation = false;
                    addedList.add(path);
                }
            }
            if(shouldIAddANewLocation) {
                List<PathDto> newList = new ArrayList<>();
                newList.add(path);
                currentList.add(newList);
            }
        }
        for (List<PathDto> pathList : currentList) {
            PointDto startPoint = pathList.get(0).getPoints().get(0);

            LongitudeLatitudeMinMax coordinates = getCoordinatesInRange(startPoint);

            List<PathDto> pathDtoList = new ArrayList<>();

            List<Path> paths = pathManagementRepository.findAllPathsThatStartsInRange(coordinates.getLongitudeMin(), coordinates.getLongitudeMax(), coordinates.getLatitudeMin(), coordinates.getLatitudeMax());
            for (Path path : paths) {
                List<Long> pointsIds = pathPointRepository.findAllByPathId(path.getId());

                List<Point> points = new ArrayList<>();

                for (Long pointId : pointsIds) {
                    Optional<Point> point = pointRepository.findById(pointId);
                    point.ifPresent(points::add);
                }

                PathDto pathDto = new PathDto();
                pathDto.setDate(path.getDate());
                List<PointDto> pointDtoList = new ArrayList<>();
                for (Point point : points) {
                    pointDtoList.add(pathMapper.pointToPointDto(point));
                }
                pathDto.setPoints(pointDtoList);
                pathDtoList.add(pathDto);
            }
            meetupLocationsDto.add(checkIfPathsIntersectsWithInfectedList(pathList, pathDtoList));
        }
        return meetupLocationsDto;
    }

    public MeetupLocationsDto checkIfPathsIntersectsWithInfectedList(List<PathDto> me, List<PathDto> allInfectedPaths) {
        MeetupLocationsDto meetupLocationsDto = new MeetupLocationsDto();
        Integer[] pathStartIndex = new Integer[allInfectedPaths.size()];
        Long[] pathStartDate = new Long[allInfectedPaths.size()];
        Long[] pathStopDate = new Long[allInfectedPaths.size()];

        for(PathDto path : me) {
            Long myPathStartDate = path.getDate();
            if(myPathStartDate == null)
                continue;
            Long myPathStopDate = myPathStartDate + (path.getPoints().size()-1) * Constants.TICK_RATE * 1000;

            Arrays.fill(pathStartIndex, -1);

            for(int i = 0; i<allInfectedPaths.size(); ++i) {
                pathStartDate[i] = allInfectedPaths.get(i).getDate();
                if(pathStartDate[i] == null)
                    continue;
                pathStopDate[i] = pathStartDate[i] + (allInfectedPaths.get(i).getPoints().size()-1) * Constants.TICK_RATE * 1000;

                if(!(myPathStartDate - Constants.LIFESPAN_IN_AIR > pathStopDate[i] || myPathStopDate < pathStartDate[i])) {
                    if(myPathStartDate - Constants.LIFESPAN_IN_AIR > pathStartDate[i])
                        pathStartIndex[i] = getIndexFromCalendar(pathStartDate[i], myPathStartDate - Constants.LIFESPAN_IN_AIR);
                    else
                        pathStartIndex[i] = 0;
                }
            }

            for(int i = 0; i<allInfectedPaths.size(); ++i) {
                if (pathStartIndex[i] >= 0) {
                    //long myStartTime = myPathStartDate.getTimeInMillis();
                    //long infectedStartTime = pathStartDate[i].getTimeInMillis();
                    for(int myTimeIndex = Math.max(0, getIndexFromCalendar(myPathStartDate, pathStartDate[i])); myTimeIndex < path.getPoints().size(); ++myTimeIndex) {
                        if(path.getPoints().get(myTimeIndex).getLatitude() == 1000)
                            continue;
                        long currentTime = myPathStartDate + Constants.TICK_RATE * 1000 * myTimeIndex;
                        int infectedIndex = getIndexFromCalendar(pathStartDate[i], currentTime);
                        while(infectedIndex >= Math.max(0, getIndexFromCalendar(pathStartDate[i], currentTime-Constants.LIFESPAN_IN_AIR))) {
                            if(infectedIndex >= allInfectedPaths.get(i).getPoints().size() || allInfectedPaths.get(i).getPoints().get(infectedIndex).getLatitude() == 1000){
                                infectedIndex--;
                                continue;
                            }
                            if(isWithinRange(path.getPoints().get(myTimeIndex), allInfectedPaths.get(i).getPoints().get(infectedIndex)))
                            {
                                Long aux = myPathStartDate + Constants.TICK_RATE * myTimeIndex * 1000;
                                meetupLocationsDto.getPoints().add(new MeetupPointDto(path.getPoints().get(myTimeIndex), aux));
                                break;
                            }
                            infectedIndex--;
                        }
                    }
                }
            }
        }
        return meetupLocationsDto;
    }

    double measure(PointDto pt1, PointDto pt2){  // generally used geo measurement function
        Double R = 6378.137d; // Radius of earth in KM
        Double dLat = pt2.getLatitude() * Math.PI / 180 - pt1.getLatitude() * Math.PI / 180;
        Double dLon = pt2.getLongitude() * Math.PI / 180 - pt1.getLongitude() * Math.PI / 180;
        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(pt1.getLatitude() * Math.PI / 180) * Math.cos(pt2.getLatitude() * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c;
        return d * 1000; // meters
    }

    private boolean isWithinRange(PointDto a, PointDto b) {
        if(measure(a, b) <= Constants.MIN_DIST_FOR_INFECTION)
            return true;
        return false;
    }

    private int getIndexFromCalendar(long startTime, long currentTime) {
        if(startTime > currentTime)
            return -1;
        return (int) ((currentTime - startTime)/(Constants.TICK_RATE * 1000));
    }

    private int getIndexFromCalendar(Calendar startTime, long currentTime)
    {
        return getIndexFromCalendar(startTime.getTimeInMillis(), currentTime);
    }

    private int getIndexFromCalendar(Calendar startTime, Calendar currentTime)
    {
        return getIndexFromCalendar(startTime.getTimeInMillis(), currentTime.getTimeInMillis());
    }
}
