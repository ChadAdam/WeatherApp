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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.weather.utils.DateUtils;
import com.demo.weather.utils.FormatUtils;
import com.demo.weather.utils.WeatherContract;

import java.util.Arrays;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, GestureDetector.OnGestureListener  {
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
    ImageView icon_IV;
    private String mForecastSummary;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //main_TV = (TextView)findViewById(R.id.tv_detail);
        //
        icon_IV = (ImageView) findViewById(R.id.weather_icon_primary);
        date_TV = (TextView)findViewById(R.id.date_primary);
        desc_TV = (TextView)findViewById(R.id.humid_primary);
        high_TV = (TextView)findViewById(R.id.high_temperature_primary);
        low_TV = (TextView)findViewById(R.id.low_temperature_primary);

        hum_TV = (TextView)findViewById(R.id.humidity_extra);
        pressure_TV = (TextView)findViewById(R.id.pressure);
        wind_TV = (TextView)findViewById(R.id.wind_measurement);


        Intent i =  getIntent();
        if( i != null){
            //if(i.hasExtra(Intent.EXTRA_TEXT)){
            //extra = i.getStringExtra(Intent.EXTRA_TEXT).toString();
            //main_TV.setText(extra);
             mUri =  i.getData();
            //}
        } else{ throw new NullPointerException("URI for DetailActivity cannot be null");}
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);


        gestureDetector = new GestureDetector(this);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_menu, menu);
       // MenuItem menuItem1 = menu.findItem(R.id.action_share);
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
        int weatherId = o.getInt(Arrays.asList(DETAILS_PROJECTIONS).indexOf(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        int weatherImageId = FormatUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
        //

        date_TV.setText(s1);
        desc_TV.setText(s5);
        high_TV.setText(s3);
        low_TV.setText(s4);
        hum_TV.setText(s5);
        pressure_TV.setText(s6);
        wind_TV.setText(s7);
        icon_IV.setImageResource(weatherImageId);
        mForecastSummary = String.format("%s - %s - %s/%s",
                s1, s7, s3, s4);



    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float vX, float vY) {
        boolean result = false;
        final int SWIPE_THRESHOLD = 100;
        final int SWIPE_VELOCITY_THRESHOLD = 100;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // right or left swipe
            if (Math.abs(diffX)> SWIPE_THRESHOLD && Math.abs(vX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        } else {
            // up or down swipe
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(vY)> SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        }

        return result;
    }
    private void onSwipeTop() {
        Toast.makeText(this, "Swipe Top", Toast.LENGTH_LONG).show();
    }

    private void onSwipeBottom() {
        Toast.makeText(this, "Swipe Bottom", Toast.LENGTH_LONG).show();
    }

    private void onSwipeLeft() {
        Toast.makeText(this, "Swipe Left", Toast.LENGTH_LONG).show();
    }

    private void onSwipeRight() {
        Toast.makeText(this, "Swipe Right", Toast.LENGTH_LONG).show();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}