package com.anticoronabrigade.frontend;

import com.anticoronabrigade.frontend.ActivityClasses.Main.ApiUtils;
import com.anticoronabrigade.frontend.ActivityClasses.Main.InfectedDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.InfectedPath;
import com.anticoronabrigade.frontend.ActivityClasses.Main.User;
import com.anticoronabrigade.frontend.ActivityClasses.Main.UserAPI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RetrofitForTest {
    static UserAPI userAPI = ApiUtils.getAPIService();

    public static void register(User user, Boolean testCase){
        userAPI.createUser(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assertEquals(response.isSuccessful(), testCase);
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                fail();
            }
        });
    }

    public static void login(User user, Boolean testCase){
        userAPI.findUser(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                assertEquals(response.isSuccessful(), testCase);
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                fail();
            }
        });
    }

    public static void changeInfected(InfectedDto infectedDto, Boolean testCase){
        userAPI.changeInfected(infectedDto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assertEquals(response.isSuccessful(), testCase);
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                fail();
            }
        });
    }
}
