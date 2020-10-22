package com.demo.weather.utils;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class OpenWeatherJsonUtils {


    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */

   static final String OMW_LIST = "list";
    static final String OWM_CITY = "city";
   static final String UNIT = "°F";
   static  final String OWM_MAIN = "main";

    /* All temperatures are children of the "temp" object */
    static final String OWM_NAME = "name";
    static final String OWM_CORD = "coord";

    /* Max temperature for the day */
    static final String OWM_MAX = "temp_max";
   static  final String OWM_MIN = "temp_min";

    static final String OWM_WEATHER_ID = "id";

    static final String OWM_WEATHER = "weather";
    // final String OWM_DESCRIPTION = "main";
    static final String OWM_LATITUDE = "lat";
    static final String OWM_LONGITUDE = "lon";

    static final String OWM_PRESSURE = "pressure";
    static final String OWM_HUMIDITY = "humidity";
    static final String OWM_WINDSPEED = "speed";
    static final String OWM_WIND_DIRECTION = "deg";

    static final String OWM_TEMPERATURE = "temp";
    static final String OWM_WIND = "wind";


    static final String OWM_MESSAGE_CODE = "cod";
    public static ArrayList<String> getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException, ParseException {

        /* Weather information. Each day's forecast info is an element of the "list" array */

        //final String OWM_LIST = "list";

        /* All temperatures are children of the "temp" object */
        //final String OWM_TEMPERATURE = "temp";
        final String OMW_LIST = "list";
        final String UNIT = "°F";
        final String OWM_MAIN = "main";

        /* All temperatures are children of the "temp" object */
        final String OWM_NAME = "name";
        final String OWM_CORD = "coord";

        /* Max temperature for the day */
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
       // final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedWeatherData = null;



        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

         //JSONArray weatherArray = new JSONArray(forecastJson);
        ArrayList<String> return_val = new ArrayList<>();
        JSONArray forcastArray = forecastJson.getJSONArray(OMW_LIST);
        for( int i=0;i<forcastArray.length();i++){
            long dateTimeMillis;
            double high;
            double low;
            String lat;
            String lon;
            String description;
            double temperature;
            double feelslike;
            String location_name;
            long sunriseMillis , sunsetMillis;
            String date;

            JSONObject current_day = forcastArray.getJSONObject(i);

            JSONArray weather_json = current_day.getJSONArray(OWM_WEATHER);
            JSONObject obj2 = weather_json.getJSONObject(0);
            String d= obj2.getString("description");
            description= d.substring(0, 1).toUpperCase() + d.substring(1);


            JSONObject obj3 = current_day.getJSONObject(OWM_MAIN);
            String humid = obj3.getString("humidity")+"%";
            temperature=obj3.getDouble("temp");
            feelslike=obj3.getDouble("feels_like");
            high=obj3.getDouble("temp_min");
            low=obj3.getDouble("temp_max");
            String highLow = kelvinToFar(high)+UNIT+" is the high and "+kelvinToFar(low)+UNIT+
                    " is the low for the day with "+description+".\n";
            String temp_feels_like = "The temperature is "+kelvinToFar(temperature)+UNIT+
                    ", and the humidity is "+humid+" but it feels like "+kelvinToFar(feelslike)+UNIT;

            if(current_day.has(OWM_CORD)){
                JSONObject obj = current_day.getJSONObject(OWM_CORD);
                location_name = current_day.getString(OWM_NAME);
                lat = obj.getString("lat");
                lon = obj.getString("lon");
                String latlon = "The latitude is " + lat + " and the longitude is " + lon + " ("+location_name+")";

                DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
                sunriseMillis=obj.getLong("sunrise");
                String sunrisedate= dateFormat.format(new Date(sunriseMillis*1000L));
                sunsetMillis=obj.getLong("sunset");
                String sunsetdate= dateFormat.format(new Date(sunsetMillis*1000L));



            }




            DateFormat dateTxtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            DateFormat date2Show =  new SimpleDateFormat("MM-dd hh:mm:ss aa", Locale.ENGLISH);

            date=current_day.getString("dt_txt");
            Date formatedDate = dateTxtFormat.parse(date);


            JSONObject obj5 = current_day.getJSONObject("wind");
            double wind_speed = obj5.getDouble("speed");
            double wind_degree = obj5.getDouble("deg");
            String wind = "The wind speed is "+wind_speed+" meters per second. The wind direction is "+wind_degree+"°.";
            //return_val.add(wind);

            return_val.add(date2Show.format(formatedDate)+ ": "+kelvinToFar(temperature)+UNIT+" --"+description );

        }






              //return_val.add(location_name);

                //return_val.add(latlon);






        ///return parsedWeatherData;
        return return_val;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }
        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OMW_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_CORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
        PreferenceLoc.setLocationDetails(context,cityLatitude,cityLongitude);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];
        long normalizedUtcStartDay = DateUtils.getNormalizedUtcDateForToday();
        for (int i = 0; i < jsonWeatherArray.length(); i++) {

            long dateTimeMillis;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            int weatherId;
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);
            dateTimeMillis = normalizedUtcStartDay + DateUtils.DAY_IN_MILLIS * i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);


            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            weatherContentValues[i] = weatherValues;
        }
            return weatherContentValues;
    }


    public static ContentValues[] getWeatherDataFromJson(Context context, String forecastJsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }
        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OMW_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_CORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
        PreferenceLoc.setLocationDetails(context,cityLatitude,cityLongitude);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];
        long normalizedUtcStartDay = DateUtils.getNormalizedUtcDateForToday();
        for (int i = 0; i < jsonWeatherArray.length(); i++) {

            long dateTimeMillis;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            int weatherId;
            JSONObject dayForecastM = jsonWeatherArray.getJSONObject(i);
            JSONObject dayForecast = dayForecastM.getJSONObject(OWM_MAIN);
            JSONObject daySpeed = dayForecastM.getJSONObject(OWM_WIND);
            dateTimeMillis = normalizedUtcStartDay + DateUtils.DAY_IN_MILLIS * i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);

            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = daySpeed.getDouble(OWM_WINDSPEED);
            windDirection = daySpeed.getDouble(OWM_WIND_DIRECTION);

            JSONObject weatherObject =
                    dayForecastM.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);


            //JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = dayForecast.getDouble(OWM_MAX);
            low = dayForecast.getDouble(OWM_MIN);
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            weatherContentValues[i] = weatherValues;
        }
        return weatherContentValues;
    }
    private static String kelvinToFar(double temp){
        double far = 1.8*(temp - 273) + 32;
        DecimalFormat df = new DecimalFormat("####0.0");
        return df.format(far);
    }
}

