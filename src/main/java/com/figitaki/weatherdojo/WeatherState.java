package com.figitaki.weatherdojo;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherState {
    public int temp;
    public int temp_max;
    public int temp_min;
    public int humidity;
    public int windspeed;
    public int winddeg;
    public String condition;
    public int icon;
    public String name;

    public WeatherState(int t, int max, int min, int hum, int wspd, int wdeg,
                        String desc, String cond) {
        temp = t;
        temp_max = max;
        temp_min = min;
        humidity = hum;
        windspeed = wspd;
        winddeg = wdeg;
        condition = cond;
    }

    public WeatherState(String JSONString) {
        JSONObject mJSON, main, weather, wind;
        try {
            mJSON = new JSONObject(JSONString);
            main = mJSON.getJSONObject("main");
            weather = mJSON.getJSONArray("weather").getJSONObject(0);
            wind = mJSON.getJSONObject("wind");
            temp = main.getInt("temp");
            temp_max = main.getInt("temp_max");
            temp_min = main.getInt("temp_min");
            humidity = main.getInt("humidity");
            windspeed = wind.getInt("speed");
            winddeg = wind.getInt("deg");
            condition = weather.getString("main");
            name = mJSON.getString("name");
            String temp = weather.getString("icon");
            if (temp.matches("01[n|d]")) icon = 0;
            else if (temp.matches("0[2-3][n|d]")) icon = 1;
            else if (temp.matches("(09|10)[n|d]")) icon = 2;
            else if (temp.matches("11[n|d]")) icon = 4;
            else if (temp.matches("13[n|d]")) icon = 3;
            else icon = 5;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        String serial = "{main:{temp:" + temp + ",temp_max:" + temp_max +
                ",temp_min:" + temp_min + ",humidity:" + humidity + "},wind:{speed:" +
                windspeed + ",deg:" + winddeg + "},weather:[{main:\"" + condition +
                "\",icon:" + icon + "}],name:" + name +  "}";

        return serial;
    }

}
