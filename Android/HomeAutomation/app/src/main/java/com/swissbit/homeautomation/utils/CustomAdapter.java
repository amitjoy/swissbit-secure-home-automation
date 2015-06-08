package com.swissbit.homeautomation.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.swissbit.homeautomation.R;

import java.util.ArrayList;

/**
 * Created by manit on 05/06/15.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    private String rId;
    private String rName;
    private String rDesc;

    private View customView;

    public static void setImageStatus(int imgId) {
        imageStatus.setImageResource(imgId);
    }

    private static ImageView imageStatus;

    public CustomAdapter(Context context, ArrayList<String> raspberryInfo) {
        super(context, R.layout.raspberry_details, new String[] {raspberryInfo.get(0)}); //The third parameter can be a list also
        rId = raspberryInfo.get(0);
        rName = raspberryInfo.get(1);
        rDesc = raspberryInfo.get(2);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        customView = layoutInflater.inflate(R.layout.raspberry_details, parent, false);

        String singleRaspberry = getItem(position);
        TextView raspberryName = (TextView) customView.findViewById(R.id.txtRaspberryName);
        TextView raspberryDescription = (TextView) customView.findViewById(R.id.txtRaspberryDescription);
        TextView raspberryId = (TextView) customView.findViewById(R.id.txtRaspberryId);
        TextView raspberryStatus = (TextView) customView.findViewById(R.id.txtRaspberryStatus);
        ImageView imageRaspberry = (ImageView) customView.findViewById(R.id.imgRaspberry);
        imageStatus = (ImageView) customView.findViewById(R.id.imgStatus);

        raspberryName.setText(rName);
        raspberryId.setText(rId);
        return customView;
    }

}
