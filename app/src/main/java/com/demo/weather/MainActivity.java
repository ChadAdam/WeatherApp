package com.demo.weather;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.weather.utils.NetUtils;
import com.demo.weather.utils.OpenWeatherJsonUtils;
import com.demo.weather.utils.PreferenceLoc;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements WeatherAdapter.WeatherAdapterOnClickHandler {
    private RecyclerView mWeatherRV;
    private TextView mErrorTV;
    private ProgressBar mLoading;
    private WeatherAdapter mWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

    mWeatherRV =  (RecyclerView) findViewById(R.id.rv_weather_data);
    mErrorTV = (TextView) findViewById(R.id.tv_weather_error);
    mLoading =  (ProgressBar) findViewById(R.id.progress_bar);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL, false);
    mWeatherRV.setLayoutManager(layoutManager);
    //mWeatherRV.setHasFixedSize(true);
    mWeatherAdapter = new WeatherAdapter(this);
    mWeatherRV.setAdapter(mWeatherAdapter);





    loadWeatherData();
    }

    private void errorVisible(){
            mWeatherRV.setVisibility(View.INVISIBLE);
            mErrorTV.setVisibility(View.VISIBLE);
    }
    private void messageVisible(){
        mErrorTV.setVisibility(View.INVISIBLE);
        mWeatherRV.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int idclicked = item.getItemId();
        if(idclicked==R.id.action_refresh){
            mWeatherAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }
        if(idclicked==R.id.action_map){
            openMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void  openMap(){
        String address =  "1600 Ampitheatre Parkway, CA";
        Uri.Builder builder = new Uri.Builder();
        Uri geo = builder.scheme("geo").path("0,0").query(address).build();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(geo);

        if(i.resolveActivity(getPackageManager())!=null){
            startActivity(i);
        }
    }
    private void loadWeatherData() {
        String location = PreferenceLoc.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    @Override
    public void onClick(String weatherLine) {
        Context c = this;
        //Toast.makeText(c, weatherLine, Toast.LENGTH_LONG).show();
        Intent i = new Intent(c , DetailActivity.class);
        i.putExtra(Intent.EXTRA_TEXT,weatherLine);
        startActivity(i);

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoading.setVisibility(View.VISIBLE);
        }

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
            mLoading.setVisibility(View.INVISIBLE);
        if(s != null) {
            messageVisible();
           mWeatherAdapter.setWeatherData(s);
        }
        else{
            errorVisible();
        }

        }
    }
}