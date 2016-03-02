package net.techguild.base.data.api;

import net.techguild.base.data.request.LoginRequest;
import net.techguild.base.data.response.LoginResponse;
import net.techguild.base.data.response.UserResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface UserService {
    @POST("/user/login") Observable<LoginResponse> login(@Body LoginRequest request);

    @GET("/user/") Observable<UserResponse> getUser();
}
