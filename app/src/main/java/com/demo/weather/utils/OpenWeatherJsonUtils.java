package com.demo.weather.utils;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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

    public static ArrayList<String> getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */

        //final String OWM_LIST = "list";

        /* All temperatures are children of the "temp" object */
        //final String OWM_TEMPERATURE = "temp";
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
        int key_count = 0;


        //{"temp":286.85,"feels_like":284.56,"temp_min":285.37,"temp_max":288.15,"pressure":1020,"humidity":82}

        //parsedWeatherData = new String[weatherArray.length()];



       // long localDate = System.currentTimeMillis();
        //long utcDate = DateUtils.getUTCDateFromLocal(localDate);
        //long startDay = DateUtils.normalizeDate(utcDate);
        String date;
        String highAndLow;

        //These are the values that will be collected
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
        ArrayList<String> return_val = new ArrayList<>();

        //for (int i = 0; i < key_count; i++) {


            // Get the JSON object representing the day
            //JSONArray dayForecastKey = forecastJson.getJSONArray(OWM_CORD);
            //int l1 = dayForecastKey.length();
           // for(int i=0;i<l1;i++) {
                //String key = (String) dayForecastKey.keys().next();

                location_name = forecastJson.getString(OWM_NAME);
              //return_val.add(location_name);
                JSONObject obj = forecastJson.getJSONObject(OWM_CORD);

                lat = obj.getString("lat");
                lon = obj.getString("lon");
                String latlon = "The latitude is " + lat + " and the longitude is " + lon + " ("+location_name+")";
                return_val.add(latlon);

          
               //JSONObject obj2 = forecastJson.getJSONObject(OWM_WEATHER);
                // Description stuff
                JSONArray weather_json = forecastJson.getJSONArray(OWM_WEATHER);
                JSONObject obj2 = weather_json.getJSONObject(0);
               description= obj2.getString("description");
               //return_val.add(description);
            //}
            //else if(OWM_MAIN.equals(key)){
            // Temperature stuff
              JSONObject obj3 = forecastJson.getJSONObject(OWM_MAIN);
              temperature=obj3.getDouble("temp");
              feelslike=obj3.getDouble("feels_like");
              high=obj3.getDouble("temp_min");
              low=obj3.getDouble("temp_max");
              String highLow = kelvinToFar(high)+UNIT+" is the high and "+kelvinToFar(low)+UNIT+ " is the low for the day with "+description+".\n";
              String temp_feels_like = "The temperature is "+kelvinToFar(temperature)+UNIT+" but it feels like "+kelvinToFar(feelslike)+UNIT;
              return_val.add(highLow+temp_feels_like);

        // Sunrise , Sunset and Current dates
        dateTimeMillis=forecastJson.getLong("dt");
        date= new Date(dateTimeMillis*1000L).toString();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        JSONObject obj4 = forecastJson.getJSONObject("sys");
        sunriseMillis=obj4.getLong("sunrise");
        String sunrisedate= dateFormat.format(new Date(sunriseMillis*1000L));

        sunsetMillis=obj4.getLong("sunset");
        String sunsetdate= dateFormat.format(new Date(sunsetMillis*1000L));

        String times = "The forecast timestamp is "+date+".  "+" Sunrise is at "+sunrisedate+ " , and sunset is at "+sunsetdate+" for the day.";
        return_val.add(times);

        // Wind Stuff
        JSONObject obj5 = forecastJson.getJSONObject("wind");
        double wind_speed = obj5.getDouble("speed");
        double wind_degree = obj5.getDouble("deg");
        String wind = "The wind speed is "+wind_speed+" meters per second. The wind direction is "+wind_degree+"°.";
        return_val.add(wind);



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
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
    private static String kelvinToFar(double temp){
        double far = 1.8*(temp - 273) + 32;
        DecimalFormat df = new DecimalFormat("####0.0");
        return df.format(far);
    }
}

