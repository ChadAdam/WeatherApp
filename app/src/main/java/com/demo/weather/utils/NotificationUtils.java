package com.demo.weather.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.demo.weather.R;

public class NotificationUtils {
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY
    };
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;
    public static final int INDEX_HUMIDITY = 3;

    public static void notifyUserOfNewWeather(Context context) {
    Uri todaysWeatherUri = WeatherContract.WeatherEntry
            .buildWeatherUriWithDate(DateUtils.normalizeDate(System.currentTimeMillis()));
    Cursor todayWeatherCursor = context.getContentResolver().query(
            todaysWeatherUri,
            WEATHER_NOTIFICATION_PROJECTION,
            null,
            null,
            null);
    if(todayWeatherCursor.moveToFirst()){
        int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
        double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
        double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);
        double humid = todayWeatherCursor.getDouble(INDEX_HUMIDITY);

        Resources resources = context.getResources();
        int largeArtResourceId = FormatUtils
                .getLargeArtResourceIdForWeatherCondition(weatherId);
        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                largeArtResourceId);

        String notificationTitle = context.getString(R.string.app_name);

        String notificationText = getNotificationText(context, weatherId, high, low, humid);

        int smallArtResourceId = FormatUtils
                .getSmallArtResourceIdForWeatherCondition(weatherId);



//          TODO (3) Create an Intent with the proper URI to start the DetailActivity

//          TODO (4) Use TaskStackBuilder to create the proper PendingIntent

//          TODO (5) Set the content Intent of the NotificationBuilder

//          TODO (6) Get a reference to the NotificationManager

//          TODO (7) Notify the user with the ID WEATHER_NOTIFICATION_ID

//          TODO (8) Save the time at which the notification occurred using SunshinePreferences
    }
        todayWeatherCursor.close();
    }

    private static String getNotificationText(Context context, int weatherId, double high, double low, double humid) {

        /*
         * Short description of the weather, as provided by the API.
         * e.g "clear" vs "sky is clear".
         */
        //String shortDescription = FormatUtils
                //.getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the forecast summary */
        String notificationText = String.format(notificationFormat,
                FormatUtils.formatTemp(context, high),
                FormatUtils.formatTemp(context, low),
                FormatUtils.formatHumidity(context,humid));

        return notificationText;
    }
}
