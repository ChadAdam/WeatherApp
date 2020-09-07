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
            description= obj2.getString("description");


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

            return_val.add(date2Show.format(formatedDate)+ ": "+kelvinToFar(temperature)+UNIT );

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

