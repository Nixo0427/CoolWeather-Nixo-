package com.android.coolweather.util;

import android.text.TextUtils;

import com.android.coolweather.db.City;
import com.android.coolweather.db.County;
import com.android.coolweather.db.Province;
import com.android.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nixo on 2017/11/1.
 */

public class Utility {

    /**
     *
     * 解析和处理服务器反回的省级数据
     */

    public  static  boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){ //判断获取到的结果是否为空或者0
            try {
                JSONArray allProvince = new JSONArray(response);  //初始化JsonArray把结果穿进去
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                    }
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * 解析和处理服务器返回的市级数据
     */

    public static boolean handleCityResponse(String response , int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i =0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city =new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     *
     *解析和处理服务器返回的县级数据
     */

    public static boolean handleCountyResponse (String response , int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i =0;i<allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityID(cityId);
                    county.save();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 将返回的Json数据解析封装成Weather类的对象
     */

    public static Weather handleWeatherResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray  jsonArray  = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }


}
