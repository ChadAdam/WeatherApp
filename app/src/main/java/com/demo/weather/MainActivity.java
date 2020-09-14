package com.demo.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements WeatherAdapter.WeatherAdapterOnClickHandler ,
        LoaderManager.LoaderCallbacks<ArrayList<String>> {
    private RecyclerView mWeatherRV;
    private TextView mErrorTV;
    private ProgressBar mLoading;
    private WeatherAdapter mWeatherAdapter;
    private final int LOADERID = 1;
    private final String QUERYEXTRAID = "q";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

    mWeatherRV =  (RecyclerView) findViewById(R.id.rv_weather_data);
    mErrorTV = (TextView) findViewById(R.id.tv_weather_error);
    mLoading =  (ProgressBar) findViewById(R.id.progress_bar);
    TextView mDetailTV = (TextView) findViewById(R.id.tv_detail);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL, false);
    mWeatherRV.setLayoutManager(layoutManager);
    //mWeatherRV.setHasFixedSize(true);
    mWeatherAdapter = new WeatherAdapter(this);
    mWeatherRV.setAdapter(mWeatherAdapter);

    if(savedInstanceState!=null){
        String query= (String) savedInstanceState.get(QUERYEXTRAID);
        //mDetailTV.setText(query);
    }
    LoaderCallbacks<ArrayList<String>> callback = MainActivity.this;
    getSupportLoaderManager().initLoader(LOADERID, null, callback);

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
            getSupportLoaderManager().restartLoader(LOADERID, null , this);
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


    @Override
    public void onClick(String weatherLine) {
        Context c = this;
        //Toast.makeText(c, weatherLine, Toast.LENGTH_LONG).show();
        Intent i = new Intent(c , DetailActivity.class);
        i.putExtra(Intent.EXTRA_TEXT,weatherLine);
        startActivity(i);

    }



    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {

            ArrayList<String > mweatherData=null;
            @Override
            protected void onStartLoading() {
                if(mweatherData!=null){
                    deliver(mweatherData);
                    //return;
                }
                else{
                    mLoading.setVisibility(View.VISIBLE);
                    forceLoad();
                }
                //mLoading.setVisibility(View.VISIBLE);


            }

            @Nullable
            @Override
            public ArrayList<String> loadInBackground() {
                //String searchQuery = bundle.getString(QUERYEXTRAID);
                String searchQuery = PreferenceLoc.getPreferredWeatherLocation(MainActivity.this);
                if(searchQuery.length()==0){
                    return null;
                }

                try{
                    URL weatherURL = NetUtils.buildUrl(searchQuery);
                    String jsonWeatherResponse = NetUtils
                            .getResponseFromHttpUrl(weatherURL);
                    ArrayList<String> simpleJsonWeatherData = OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                    return simpleJsonWeatherData;

                }
                catch (IOException | ParseException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliver(ArrayList<String> s){
                mweatherData = s;
                super.deliverResult(s);

            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> s) {
        mLoading.setVisibility(View.INVISIBLE);
        mWeatherAdapter.setWeatherData(s);
        if(s != null){
            messageVisible();

        }
        else{  errorVisible();}

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) {

    }


}