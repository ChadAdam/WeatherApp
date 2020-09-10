package com.demo.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
}