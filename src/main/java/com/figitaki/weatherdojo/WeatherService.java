package com.figitaki.weatherdojo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService extends IntentService {

    private String result;
    public static final String EXTRA_MESSENGER = "com.figitaki.weatherdojo.HANDLE";

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: something useful
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        InputStream is = null;
        int len = 5000;

        try {
            URL url = new URL(intent.getDataString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("com.figtiaki.weatherdojo", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            result = readIt(is, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger)extras.get(EXTRA_MESSENGER);
        Message msg = Message.obtain();

        Bundle data = new Bundle();
        data.putString("result", result);
        if (msg != null) msg.setData(data);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String readIt(InputStream stream, int len) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        return reader.readLine();
    }

}
