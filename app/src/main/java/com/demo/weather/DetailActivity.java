package com.demo.weather;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.demo.weather.utils.DateUtils;
import com.demo.weather.utils.FormatUtils;
import com.demo.weather.utils.WeatherContract;

import java.util.Arrays;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
    //private TextView main_TV;
    private String extra;

    public static final int ID_DETAIL_LOADER = 22;
    public static final String[] DETAILS_PROJECTIONS={
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED


    };
    WeatherAdapter mWeatherAdapter;
    TextView date_TV;
    TextView desc_TV;
    TextView high_TV;
    TextView low_TV;
    TextView hum_TV;
    TextView pressure_TV;
    TextView wind_TV;
    Uri mUri;
    private String mForecastSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //main_TV = (TextView)findViewById(R.id.tv_detail);
        date_TV = (TextView)findViewById(R.id.tv_date);
        desc_TV = (TextView)findViewById(R.id.tv_desc);
        high_TV = (TextView)findViewById(R.id.tv_high);
        low_TV = (TextView)findViewById(R.id.tv_low);
        hum_TV = (TextView)findViewById(R.id.tv_hum);
        pressure_TV = (TextView)findViewById(R.id.tv_pressure);
        wind_TV = (TextView)findViewById(R.id.tv_wind);


        Intent i =  getIntent();
        if( i != null){
            //if(i.hasExtra(Intent.EXTRA_TEXT)){
            //extra = i.getStringExtra(Intent.EXTRA_TEXT).toString();
            //main_TV.setText(extra);
             mUri =  i.getData();
            //}
        } else{ throw new NullPointerException("URI for DetailActivity cannot be null");}
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_menu, menu);
       // MenuItem menuItem = menu.findItem(R.id.action_share);
        //menuItem.setIntent(createShareForecastIntent());
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if ( id == R.id.action_share){
          Intent i =  ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(mForecastSummary)
                    .getIntent();
          startActivity(i);
            return true;
        }
        if(id ==R.id.action_settings){
            Intent i = new Intent(this , SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(extra + "#")
                .getIntent();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i){
            case ID_DETAIL_LOADER:
                //Uri detailQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                //String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                //String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this ,mUri, DETAILS_PROJECTIONS,null,null,null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + i);



        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor o) {
        boolean cursorValidData = false;
        if(o !=null && o.moveToFirst() ){
            cursorValidData = true;
        }
        if(!cursorValidData){
            return;
        }
        //mWeatherAdapter.swapCursor(o);
        long date =o.getLong(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_DATE));
        String s1 = DateUtils.getFriendlyDateString(this , date, true);
        //String s2 =o.getString(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_DEGREES));

        double max_far =o.getDouble(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
        String s3 = FormatUtils.formatTemp(this, max_far);
        double min_far =o.getDouble(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
        String s4 = FormatUtils.formatTemp(this ,min_far);
        // Format Humdiity as Float?
        float hum =o.getFloat(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        String s5 = getString(R.string.format_humidity, hum);
        float press =o.getFloat(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        String s6 = getString(R.string.format_pressure, press);
        float winSpeed = o.getFloat(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        float winDir = o.getFloat(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_DEGREES));
        String s7 =FormatUtils.getFormattedWind(this,winSpeed,winDir);
        date_TV.setText(s1);
        //desc_TV.setText(s2);
        high_TV.setText(s3);
        low_TV.setText(s4);
        hum_TV.setText(s5);
        pressure_TV.setText(s6);
        wind_TV.setText(s7);
        mForecastSummary = String.format("%s - %s - %s/%s",
                s1, s7, s3, s4);



    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


}