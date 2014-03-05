package com.figitaki.weatherdojo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class WeatherFragment extends Fragment
{
    private ImageView weatherImageView;
    private TextView conditionTextView, temperatureTextView, tempMaxTextView, tempMinTextView,
            humidityTextView, windTextView;
    private Button updateButton;
    private ProgressBar updateProgress;

    private LocationManager locationManager;
    private Location lastLocation;
    private WeatherState weatherState;

    private Drawable[] conditions;
    private int condition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Toast.makeText(getActivity(), "Complete", Toast.LENGTH_SHORT).show();
            onRequestReturn(msg.getData().getString("result"));
        }
    };

    /** Called when the activity is first created. */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        weatherImageView = (ImageView) rootView.findViewById(R.id.image_weather);
        conditionTextView = (TextView) rootView.findViewById(R.id.label_condition);
        temperatureTextView = (TextView) rootView.findViewById(R.id.label_temperature);
        updateButton = (Button) rootView.findViewById(R.id.button_update);
        updateProgress = (ProgressBar) rootView.findViewById(R.id.update_progress);
        tempMaxTextView = (TextView) rootView.findViewById(R.id.label_temp_max);
        tempMinTextView = (TextView) rootView.findViewById(R.id.label_temp_min);
        humidityTextView = (TextView) rootView.findViewById(R.id.label_humidity);
        windTextView = (TextView) rootView.findViewById(R.id.label_wind);

        conditions = new Drawable[6];
        condition = 0;
        conditions[0] = getResources().getDrawable(R.drawable.sun);
        conditions[1] = getResources().getDrawable(R.drawable.cloudy);
        conditions[2] = getResources().getDrawable(R.drawable.cloudy_rain);
        conditions[3] = getResources().getDrawable(R.drawable.cloudy_snow);
        conditions[4] = getResources().getDrawable(R.drawable.lightning);
        conditions[5] = getResources().getDrawable(R.drawable.light_cloud);

        locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (savedInstanceState != null) {
            weatherState = new WeatherState(savedInstanceState.getString("weatherState"));
            updateView();
        } else {
            updateWeather();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("weatherState", weatherState.toString());

        super.onSaveInstanceState(outState);
    }

    /** Called whenever user wishes to refresh the WeatherStatus **/
    public void updateWeather() {
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
            Intent i = new Intent(getActivity(), WeatherService.class);
            i.setData(Uri.parse(url));
            i.putExtra(WeatherService.EXTRA_MESSENGER, new Messenger(handler));
            getActivity().startService(i);
            // new WeatherTask(this).execute(url);
        } else {
            Toast.makeText(getActivity(), "No network connection available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onRequestReturn(String result) {
        int temp = 0, max = 0, min = 0;
        weatherState = new WeatherState(result);
        updateView();
    }

    private void updateView() {
        conditionTextView.setText(weatherState.condition);
        temperatureTextView.setText(weatherState.temp + "°");
        tempMaxTextView.setText(weatherState.temp_max + "°");
        tempMinTextView.setText(weatherState.temp_min + "°");
        humidityTextView.setText("Humidity: " + weatherState.humidity + "%");
        windTextView.setText("Wind: " + weatherState.windspeed + "MPH");
        weatherImageView.setImageDrawable(conditions[condition]);
        updateProgress.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);
        weatherImageView.setImageDrawable(conditions[weatherState.icon]);
    }
}
