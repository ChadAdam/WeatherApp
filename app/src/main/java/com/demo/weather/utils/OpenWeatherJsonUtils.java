package com.demo.weather.utils;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;
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
        ArrayList<String> return_val = new ArrayList<>();

        //for (int i = 0; i < key_count; i++) {


            // Get the JSON object representing the day
            //JSONArray dayForecastKey = forecastJson.getJSONArray(OWM_CORD);
            //int l1 = dayForecastKey.length();
           // for(int i=0;i<l1;i++) {
                //String key = (String) dayForecastKey.keys().next();
                JSONObject obj = forecastJson.getJSONObject(OWM_CORD);

                lat = obj.getString("lat");
                lon = obj.getString("lon");
                String latlon = "The latitude is " + lat + " and the longitude is " + lon;
                return_val.add(latlon);

          //  }
            //else if(OWM_WEATHER.equals(key)){
               //JSONObject obj2 = forecastJson.getJSONObject(OWM_WEATHER);
                JSONArray weather_json = forecastJson.getJSONArray(OWM_WEATHER);
                JSONObject obj2 = weather_json.getJSONObject(0);
               description= obj2.getString("description");
               return_val.add(description);
            //}
            //else if(OWM_MAIN.equals(key)){
              JSONObject obj3 = forecastJson.getJSONObject(OWM_MAIN);
              temperature=obj3.getDouble("temp");
              feelslike=obj3.getDouble("feels_like");
              high=obj3.getDouble("temp_min");
              low=obj3.getDouble("temp_max");
              String highLow = high+" is the high and "+low+ " is the low for the day";
              String temp_feels_like = "The temperature is "+kelvinToFar(temperature)+UNIT+" but it feels like "+kelvinToFar(feelslike)+UNIT;
              return_val.add(temp_feels_like);

           // }
            //else if("dt".equals(key)){
        //String obj4 = forecastJson.getString("dt");
        dateTimeMillis=forecastJson.getLong("dt");
        date= new Date(dateTimeMillis*1000L).toString();
        return_val.add(date);
            //}

            /*
            dateTimeMillis = startDay + SunshineDateUtils.DAY_IN_MILLIS * i;
            date = SunshineDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */


            //JSONObject weatherObject =
             //       dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            //description = weatherObject.getString(OWM_DESCRIPTION);

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary and is just a bad variable name.
             */
            //JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            //high = temperatureObject.getDouble(OWM_MAX);
            //low = temperatureObject.getDouble(OWM_MIN);
           // highAndLow = SunshineWeatherUtils.formatHighLows(context, high, low);


            //parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;
       // }


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

