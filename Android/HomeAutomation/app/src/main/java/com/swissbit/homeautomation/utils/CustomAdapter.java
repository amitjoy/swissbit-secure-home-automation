package com.swissbit.homeautomation.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.swissbit.homeautomation.R;

/**
 * Created by manit on 05/06/15.
 */
public class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(Context context, String[] raspberryIds) {
        super(context, R.layout.raspberry_details,raspberryIds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.raspberry_details, parent, false);

        String singleRaspberry = getItem(position);
        TextView raspberryName = (TextView) customView.findViewById(R.id.txtRaspberryName);
        TextView raspberryDescription = (TextView) customView.findViewById(R.id.txtRaspberryDescription);
        ImageView imageRaspberry = (ImageView) customView.findViewById(R.id.imgRaspberry);

        raspberryName.setText("Raspberry1");
        raspberryDescription.setText("Description1");

        return customView;
    }
}
