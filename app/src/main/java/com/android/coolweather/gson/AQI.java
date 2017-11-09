package com.android.coolweather.gson;

/**
 * Created by Nixo on 2017/11/5.
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
