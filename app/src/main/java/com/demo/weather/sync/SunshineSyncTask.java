package com.demo.weather.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.demo.weather.utils.DateUtils;
import com.demo.weather.utils.NetUtils;
import com.demo.weather.utils.NotificationUtils;
import com.demo.weather.utils.OpenWeatherJsonUtils;
import com.demo.weather.utils.PreferenceLoc;
import com.demo.weather.utils.WeatherContract;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

//  TODO (1) Create a class called SunshineSyncTask
//  TODO (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//      TODO (3) Within syncWeather, fetch new weather data
//      TODO (4) If we have valid results, delete the old data and insert the new
public class SunshineSyncTask {
    synchronized public static void syncWeather(Context context){
        try{
            URL weatherRequestURL = NetUtils.getUrl(context);
            String jsonWeatherResponse = NetUtils.getResponseFromHttpUrl(weatherRequestURL);
            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherDataFromJson(context,jsonWeatherResponse);

            if (weatherValues != null && weatherValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver weatherContentResolver = context.getContentResolver();

//              COMPLETED (4) If we have valid results, delete the old data and insert the new
                /* Delete old weather data because we don't need to keep multiple days' data */
                weatherContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                weatherContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);

                boolean notificationEnabled = PreferenceLoc.areNotificationsEnabled(context);
                long timeSinceLastNotification = PreferenceLoc
                        .getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }
                if (notificationEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
