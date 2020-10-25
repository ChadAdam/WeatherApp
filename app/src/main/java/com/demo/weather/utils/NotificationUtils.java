package com.demo.weather.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.demo.weather.DetailActivity;
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
    public static final int WEATHER_NOTIFICATION_ID = 2009;
    public static final String NOT_CHANNEL_ID = "Notification_channel_ID";

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
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    NOT_CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

        }

        Intent detailIntentForToday = new Intent(context, DetailActivity.class);
        detailIntentForToday.setData(todaysWeatherUri);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//          TODO (3) Create an Intent with the proper URI to start the DetailActivity
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(smallArtResourceId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "Style 1"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


//          TODO (4) Use TaskStackBuilder to create the proper PendingIntent

//          TODO (5) Set the content Intent of the NotificationBuilder
        //notificationBuilder.setContentIntent(resultPendingIntent);


//          TODO (6) Get a reference to the NotificationManager


        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
           // notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        //}


//          TODO (7) Notify the user with the ID WEATHER_NOTIFICATION_ID
        notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());


//          TODO (8) Save the time at which the notification occurred using SunshinePreferences
        PreferenceLoc.saveLastNotificationTime(context, System.currentTimeMillis());

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
