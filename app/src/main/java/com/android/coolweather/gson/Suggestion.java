package com.android.coolweather.gson;

import android.os.Handler;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nixo on 2017/11/5.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;

    }
    public class CarWash{

        @SerializedName("txt")
        public String info;

    }

    public class Sport{

        @SerializedName("txt")
        public String info;

    }

   public void Handler(){
        
    }

}
