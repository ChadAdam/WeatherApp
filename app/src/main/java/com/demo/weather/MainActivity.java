package com.demo.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.demo.weather.utils.NetUtils;
import com.demo.weather.utils.OpenWeatherJsonUtils;
import com.demo.weather.utils.PreferenceLoc;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private TextView mWeatherTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

    mWeatherTV =  findViewById(R.id.tv_weather_data);




    loadWeatherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int idclicked = item.getItemId();
        if(idclicked==R.id.action_refresh){
            mWeatherTV.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void loadWeatherData() {
        String location = PreferenceLoc.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            //return null;
            if(urls.length==0){
                return null;
            }
            String location = urls[0];
            URL weatherRequestUrl = NetUtils.buildUrl(location);
            try {
                String jsonWeatherResponse = NetUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                ArrayList<String> simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }




        @Override
        protected void onPostExecute(ArrayList<String> s) {
        if(s != null) {
            for (String w_string : s) {
                mWeatherTV.append((w_string) + "\n\n\n");
            }
        }

        }
    }
}