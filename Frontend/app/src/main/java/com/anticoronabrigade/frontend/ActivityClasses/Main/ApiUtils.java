package com.anticoronabrigade.frontend.ActivityClasses.Main;

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL_HOME = "http://192.168.1.5:8080";
    public static final String BASE_URL_HOTSPOT = "http://192.168.43.115:8080";
    public static final String HEROKU = "https://anticoronabrigadebackend.herokuapp.com/";

    public static UserAPI getAPIService() {
        return RetrofitUserClient.getClient(HEROKU).create(UserAPI.class);
    }
}
