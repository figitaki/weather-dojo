package com.figitaki.weatherdojo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment
{
    private ImageView weatherImageView;
    private TextView temperatureTextView;
    private TextView tempMaxTextView;
    private TextView tempMinTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private Button updateButton;
    private ProgressBar updateProgress;

    private LocationManager locationManager;
    private Location lastLocation;
    private WeatherState weatherState;

    private Drawable[] conditions;
    private int condition;

    /** Called when the activity is first created. */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        weatherImageView = (ImageView) rootView.findViewById(R.id.image_weather);
        temperatureTextView = (TextView) rootView.findViewById(R.id.label_temperature);
        updateButton = (Button) rootView.findViewById(R.id.button_update);
        updateProgress = (ProgressBar) rootView.findViewById(R.id.update_progress);
        tempMaxTextView = (TextView) rootView.findViewById(R.id.label_temp_max);
        tempMinTextView = (TextView) rootView.findViewById(R.id.label_temp_min);
        humidityTextView = (TextView) rootView.findViewById(R.id.label_humidity);
        windTextView = (TextView) rootView.findViewById(R.id.label_wind);

        conditions = new Drawable[5];
        condition = 0;
        conditions[0] = getResources().getDrawable(R.drawable.cloudy);
        conditions[1] = getResources().getDrawable(R.drawable.sun);
        conditions[2] = getResources().getDrawable(R.drawable.cloudy_snow);
        conditions[3] = getResources().getDrawable(R.drawable.cloudy_rain);
        conditions[4] = getResources().getDrawable(R.drawable.lightning);

        locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        temperatureTextView.setText("??°");
        tempMaxTextView.setText("??°/");
        tempMinTextView.setText("??°");
        humidityTextView.setText("Humidity: ??%");
        windTextView.setText("Wind: ??MPH");
        weatherImageView.setImageDrawable(conditions[1]);

        return rootView;
    }

    public void updateWeather(View v) throws InterruptedException {
        updateButton.setVisibility(View.GONE);
        updateProgress.setVisibility(View.VISIBLE);

        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String url = String.format(
                "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial",
                lastLocation.getLatitude(), lastLocation.getLongitude());

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            Toast.makeText(getActivity(), "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onRequestReturn(String result) {
        int temp = 0, max = 0, min = 0;
        try {
            JSONObject main = (new JSONObject(result)).getJSONObject("main");
            JSONObject wind = (new JSONObject(result)).getJSONObject("wind");
            JSONObject weather = (new JSONObject(result)).getJSONArray("weather").getJSONObject(0);
            weatherState = new WeatherState(main.getInt("temp"),main.getInt("temp_max"),
                    main.getInt("temp_min"), main.getInt("humidity"), wind.getInt("speed"),
                    wind.getInt("deg"), weather.getString("main"), weather.getString("icon"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        temperatureTextView.setText(weatherState.temp + "°");
        tempMaxTextView.setText(weatherState.temp_max + "°/");
        tempMinTextView.setText(weatherState.temp_min + "°");
        humidityTextView.setText("Humidity: " + weatherState.humidity + "%");
        windTextView.setText("Wind: " + weatherState.windspeed + "MPH");
        weatherImageView.setImageDrawable(conditions[condition]);
        updateProgress.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);
    }

    /***
     * Private AsyncTask specifically for downloading the
     * weather information from the site.
     */
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            onRequestReturn(result);
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
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
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, "UTF-8"));
            return reader.readLine();
        }
    }
}
