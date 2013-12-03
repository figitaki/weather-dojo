package com.figitaki.weatherdojo;

public class WeatherState {
    public int temp;
    public int temp_max;
    public int temp_min;
    public int humidity;
    public int windspeed;
    public int winddeg;
    public String description;
    public String condition;

    public WeatherState(int t, int max, int min, int hum, int wspd, int wdeg,
                        String desc, String cond) {
        temp = t;
        temp_max = max;
        temp_min = min;
        humidity = hum;
        windspeed = wspd;
        winddeg = wdeg;
        description = desc;
        condition = cond;
    }
}
