package com.android.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.coolweather.gson.Basic;
import com.android.coolweather.gson.Forecast;
import com.android.coolweather.gson.Weather;
import com.android.coolweather.service.AutoUpdateService;
import com.android.coolweather.util.HttpUtil;
import com.android.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    //控件声明区
    public DrawerLayout drawerLayout;
    private Button      navButton;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    private ScrollView weatherLayout;
    private TextView   titleCity;
    private TextView   titleUpdateTime;
    private TextView   degreeText;
    private TextView   weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView   aqiText;
    private TextView   pm25Text;
    private TextView   comfortText;
    private TextView   carWashText;
    private TextView   sportText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //控件绑定区
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity     = (TextView)findViewById(R.id.title_city);
        titleUpdateTime= (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText =(TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        //nav摁扭
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //SharedPreferences存储
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);

        //判断是否存储
        if(weatherString != null) {
            //缓存了，直接调用
            Weather weather = Utility.handleWeatherResponse(weatherString);  //空指针 weather
            mWeatherId =weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //没缓存，调用网络的下载方法
            mWeatherId = getIntent().getStringExtra("weather_id");
            //先隐藏 不然空空的看着很奇怪。
            weatherLayout.setVisibility(View.INVISIBLE);
            //调用网络的请求方法
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

    }


    /**
     * 用ID方法查看天气（网络下载）
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=6e780f86ccbb40cdbb65432851e072dd";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"您的网络出错啦",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //开启子线程判断服务器是否返回成功。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"服务器未响应",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */

    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        //String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "度";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        //titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        Intent i = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(i);
        for (Forecast forecast : weather.forecastList){
           //空间添加区
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            //空间事件区
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = weather.suggestion.comfort.info;
        String carWash = weather.suggestion.carWash.info;
        String sport   = weather.suggestion.carWash.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);


    }

}
