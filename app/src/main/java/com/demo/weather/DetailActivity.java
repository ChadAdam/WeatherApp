package com.demo.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    private TextView main_TV;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        main_TV = (TextView)findViewById(R.id.tv_detail);
        Intent i =  getIntent();
        if( i != null){
            if(i.hasExtra(Intent.EXTRA_TEXT)){
            extra = i.getStringExtra(Intent.EXTRA_TEXT).toString();
            main_TV.setText(extra); } }
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
                    .setText(extra)
                    .getIntent();
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


}