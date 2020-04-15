package com.anticoronabrigade.frontend;

import com.anticoronabrigade.frontend.ActivityClasses.Main.ApiUtils;
import com.anticoronabrigade.frontend.ActivityClasses.Main.InfectedDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.PathListDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.User;
import com.anticoronabrigade.frontend.ActivityClasses.Main.UserAPI;

import org.junit.Test;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testRegister(){

        User userCreated = new User();
        userCreated.setEmail("good");
        userCreated.setPassword("good");
        RetrofitForTest.register(userCreated, true);

        User userNullPassword = new User();
        userCreated.setEmail("badPassword");
        userCreated.setPassword(null);
        RetrofitForTest.register(userNullPassword, false);

        User userNullUsername = new User();
        userNullUsername.setEmail(null);
        userNullUsername.setPassword("badUsername");
        RetrofitForTest.register(userNullUsername, false);

        User userNullBoth = new User();
        userNullBoth.setEmail(null);
        userNullBoth.setPassword(null);
        RetrofitForTest.register(userNullBoth, false);

    }

    @Test
    public void testLogin(){
        User userFound = new User();
        userFound.setEmail("good");
        userFound.setPassword("good");
        RetrofitForTest.login(userFound, true);

        User userNotFoundByEmail = new User();
        userNotFoundByEmail.setEmail("goo");
        userNotFoundByEmail.setEmail("good");
        RetrofitForTest.login(userNotFoundByEmail, false);

        User userNotFoundByPassword = new User();
        userNotFoundByPassword.setEmail("good");
        userNotFoundByPassword.setEmail("goo");
        RetrofitForTest.login(userNotFoundByPassword, false);

        User userNullPassword = new User();
        userNullPassword.setEmail("good");
        userNullPassword.setEmail(null);
        RetrofitForTest.login(userNullPassword, false);

        User userNullUsername = new User();
        userNullUsername.setEmail(null);
        userNullUsername.setEmail("good");
        RetrofitForTest.login(userNullUsername, false);

        User userNullBoth = new User();
        userNullBoth.setEmail(null);
        userNullBoth.setEmail(null);
        RetrofitForTest.login(userNullBoth, false);
    }

    @Test
    public void testChangeInfected(){
        InfectedDto userOKInfected = new InfectedDto();
        userOKInfected.setEmail("good");
        userOKInfected.setPassword("good");
        userOKInfected.setInfected(true);
        RetrofitForTest.changeInfected(userOKInfected, true);

        InfectedDto userOKNotInfected = new InfectedDto();
        userOKInfected.setEmail("good");
        userOKInfected.setPassword("good");
        userOKNotInfected.setInfected(false);
        RetrofitForTest.changeInfected(userOKNotInfected, true);

        InfectedDto userNotFound = new InfectedDto();
        userNotFound.setEmail("goo");
        userNotFound.setPassword("good");
        userNotFound.setInfected(false);
        RetrofitForTest.changeInfected(userNotFound, false);

        InfectedDto userWrongPassword = new InfectedDto();
        userWrongPassword.setEmail("good");
        userWrongPassword.setPassword("goo");
        userWrongPassword.setInfected(false);
        RetrofitForTest.changeInfected(userWrongPassword, false);

        InfectedDto userNullPassword = new InfectedDto();
        userNullPassword.setEmail("good");
        userNullPassword.setPassword(null);
        userNullPassword.setInfected(false);
        RetrofitForTest.changeInfected(userNullPassword, false);

        InfectedDto userNullUsername = new InfectedDto();
        userNullUsername.setEmail(null);
        userNullUsername.setPassword("good");
        userNullUsername.setInfected(false);
        RetrofitForTest.changeInfected(userNullUsername, false);

        InfectedDto userNullInfected = new InfectedDto();
        userNullInfected.setEmail("good");
        userNullInfected.setPassword("good");
        userNullInfected.setInfected(Boolean.valueOf(null));
        RetrofitForTest.changeInfected(userNullInfected, true);

        InfectedDto userNullEverything = new InfectedDto();
        userNullEverything.setEmail(null);
        userNullEverything.setPassword(null);
        userNullEverything.setInfected(Boolean.valueOf(null));
        RetrofitForTest.changeInfected(userNullEverything, true);
    }

    @Test
    public void testAddPaths(){
        //Path to delete
        PathListDto pathCreatedOlderThan10Days;
        PathListDto pathCreatedNew;
        PathListDto pathNullDate;
        PathListDto pathNullPathList;
        PathListDto pathNullLongitude;
        PathListDto pathNullLatitude;
        PathListDto pathUserNotFound;
        PathListDto pathUserNullPassword;
        PathListDto pathUserNullUsername;
    }

    @Test
    public void testCheckPaths(){
        PathListDto pathToFind;
        PathListDto pathNotToFind;
        PathListDto pathNullDate;
        PathListDto pathNullPathList;
        PathListDto pathNullLongitude;
        PathListDto pathNullLatitude;
        PathListDto pathUserNotFound;
        PathListDto pathUserNullPassword;
        PathListDto pathUserNullUsername;
    }

    @Test
    public void testGetPaths(){
        //Path to delete
        PathListDto pathCreatedOlderThan5Days;
        PathListDto pathCreatedNewerThan5Days;
        PathListDto pathCreatedNew;
        PathListDto pathNullDate;
        PathListDto pathNullPathList;
        PathListDto pathNullLongitude;
        PathListDto pathNullLatitude;
    }
}