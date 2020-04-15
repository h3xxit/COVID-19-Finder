package com.anticoronabrigade.frontend.ActivityClasses.Main;

import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @POST("/api/user/confirmEmail")
    Call<ResponseBody> sendConfirmationEmail(@Body EmailOrSmsDto emailOrSmsDto);
    @POST("/api/user/confirmPhoneNumber")
    Call<ResponseBody> sendConfirmationSms(@Body EmailOrSmsDto emailOrSmsDto);
    @POST("/api/user/postUser")
    Call<ResponseBody> createUser(@Body RegisterWithCodeDto user);
    @POST("/api/user/findUser")
    Call<Boolean> findUser(@Body User user);
    @PATCH("/api/user/changeIsInfected")
    Call<ResponseBody> changeInfected(@Body InfectedDto user);

    @POST("/api/user/forgotPasswordEmail")
    Call<ResponseBody> sendForgotPasswordEmail(@Body EmailOrSmsDto emailOrSmsDto);
    @POST("/api/user/forgotPasswordSms")
    Call<ResponseBody> sendForgotPasswordSms(@Body EmailOrSmsDto emailOrSmsDto);
    @PATCH("/api/user/changePassword")
    Call<ResponseBody> changePassword(@Body ChangePasswordDto changePasswordDto);

    @POST("/api/path/addPaths")
    Call<ResponseBody> addPaths(@Body PathListDto pathDto);
    @POST("/api/path/checkPath")
    Call<MeetupLocationsDto> checkPaths(@Body PathListDto pathDto);
    @GET("/api/path/getInfectedPaths/latitude={latitude}&&longitude={longitude}")
    Call<List<WalkedPath>> getInfectedPathsNearYou(@Path("latitude") Double latitude, @Path("longitude") Double longitude);

}
