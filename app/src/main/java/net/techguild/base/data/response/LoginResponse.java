package net.techguild.base.data.response;

import com.google.gson.annotations.SerializedName;

import net.techguild.base.data.model.User;

public class LoginResponse extends CResponse {
    public String result;
    public String token;
    public User user;
    @SerializedName("expire_time") public long expireTime;
}
