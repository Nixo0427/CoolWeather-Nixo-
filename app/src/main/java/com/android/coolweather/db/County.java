package com.android.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Nixo on 2017/11/1.
 */

public class County extends DataSupport {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    private int id;
    private String countyName; //县的名字
    private String weatherId;  //县的天气ID
    private int cityID;        //城市ID




}
