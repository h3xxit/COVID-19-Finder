package com.anticoronabrigade.backend;

import com.anticoronabrigade.backend.customClasses.PathIntersector;
import com.anticoronabrigade.backend.dto.*;
import com.anticoronabrigade.backend.dto.WithUserDto.PathListDtoWithUser;
import com.anticoronabrigade.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
       /* PathIntersector pathIntersector = new PathIntersector();
		List<PathDto> list1 = new ArrayList<>();
		List<PathDto> list2 = new ArrayList<>();

		PathDto myPath = new PathDto();

		//myPath.setDate("yyyy-MM-dd HH:mm:ss");
		//myPath.setDate("2020-01-01 01:01:30");
		myPath.getPoints().add(new PointDto(45.640090, 25.628085));
		myPath.getPoints().add(new PointDto(45.640645, 25.627506));
		myPath.getPoints().add(new PointDto(45.641043, 25.627076));
		myPath.getPoints().add(new PointDto(45.641418, 25.626647));
		myPath.getPoints().add(new PointDto(45.641418, 25.626647));
		myPath.getPoints().add(new PointDto(45.641748, 25.626325));
		myPath.getPoints().add(new PointDto(45.642281, 25.625768));
		myPath.getPoints().add(new PointDto(45.642768, 25.625489));
		myPath.getPoints().add(new PointDto(45.642978, 25.625832));
		list1.add(myPath);

		myPath = new PathDto();
		//myPath.setDate("2020-01-01 01:02:30");
		myPath.getPoints().add(new PointDto(45.640090, 25.628085));
		myPath.getPoints().add(new PointDto(45.640645, 25.627506));
		myPath.getPoints().add(new PointDto(45.641043, 25.627076));
		myPath.getPoints().add(new PointDto(45.641418, 25.626647));
		myPath.getPoints().add(new PointDto(45.641418, 25.626647));
		myPath.getPoints().add(new PointDto(45.641748, 25.626325));
		myPath.getPoints().add(new PointDto(45.642281, 25.625768));
		myPath.getPoints().add(new PointDto(45.642768, 25.625489));
		myPath.getPoints().add(new PointDto(45.642978, 25.625832));
		list1.add(myPath);

		PathDto infectedPath = new PathDto();
		//infectedPath.setDate("2020-01-01 00:58:30");
		infectedPath.getPoints().add(new PointDto(45.641560, 25.626830));
		infectedPath.getPoints().add(new PointDto(45.641418, 25.626647));
		infectedPath.getPoints().add(new PointDto(45.641297, 25.626818));
		infectedPath.getPoints().add(new PointDto(45.641199, 25.626511));
		infectedPath.getPoints().add(new PointDto(45.641205, 25.626065));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.640921, 25.625537));
		list2.add(infectedPath);

		infectedPath = new PathDto();
		//infectedPath.setDate("2020-01-01 01:01:30");
		infectedPath.getPoints().add(new PointDto(45.641560, 25.626830));
		infectedPath.getPoints().add(new PointDto(45.641418, 25.626647));
		infectedPath.getPoints().add(new PointDto(45.641297, 25.626818));
		infectedPath.getPoints().add(new PointDto(45.641199, 25.626511));
		infectedPath.getPoints().add(new PointDto(45.641205, 25.626065));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.641082, 25.625817));
		infectedPath.getPoints().add(new PointDto(45.640921, 25.625537));
		list2.add(infectedPath);

        MeetupLocationsDto meetupLocationsDto = pathIntersector.checkIfPathsIntersectsWithInfectedList(list1, list2);
		for (MeetupPointDto meet : meetupLocationsDto.getPoints()) {
			System.out.println(meet.getDate() + " at location " + meet.getPoint().getLatitude() + ", " + meet.getPoint().getLongitude());
		}*/
	}
}
