package com.demo.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherAdapterViewHolder> {
   ArrayList<String> mWeatherData;
   final WeatherAdapterOnClickHandler mClickHandler;

   public interface WeatherAdapterOnClickHandler{
       void onClick(String weatherLine);
    }

    public WeatherAdapter(WeatherAdapterOnClickHandler clickHandler){
        //later
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public WeatherAdapterViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        // Things to happen on ViewHolder is created
        // Executed when RecyclerView is laid out to full screen
        Context c = viewGroup.getContext();
        int layout = R.layout.weather_list_item;
        LayoutInflater inflate = LayoutInflater.from(c);
        View v = inflate.inflate(layout,viewGroup, false);
        return new WeatherAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder( WeatherAdapterViewHolder weatherAdapterViewHolder, int i) {
        //Assign WeatherAdapterViewHolder with data at position i
        // Recyclerview calls this to display data at position i
        String weatherDataLine = mWeatherData.get(i);
        weatherAdapterViewHolder.mWeatherTextView.setText(weatherDataLine);

    }
    // Gets number of items to be used by RecylerView
    @Override
    public int getItemCount() {
        if(mWeatherData==null){
            return 0;
        }
        return mWeatherData.size();
    }

    public class WeatherAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Deals with item
        public final TextView mWeatherTextView;

        public WeatherAdapterViewHolder( View itemView) {
            super(itemView);
            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int ad_pos = getAdapterPosition();
            String weatherLine = mWeatherData.get(ad_pos);
            mClickHandler.onClick(weatherLine);

        }
    }

    public void setWeatherData(ArrayList<String> wd){
        mWeatherData= wd;
        notifyDataSetChanged();
    }
}
