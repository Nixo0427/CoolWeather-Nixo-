package com.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nixo on 2017/11/5.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("utc")
        public String updateTime;
    }

}
