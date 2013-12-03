package com.figitaki.weatherdojo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }
}
