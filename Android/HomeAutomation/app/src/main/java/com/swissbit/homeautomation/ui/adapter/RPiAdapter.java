package com.swissbit.homeautomation.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.swissbit.homeautomation.R;
import com.swissbit.homeautomation.model.RaspberryPi;

import java.util.List;

/**
 * Created by manit on 05/06/15.
 */
public class RPiAdapter extends ArrayAdapter<RaspberryPi> {

    private List<RaspberryPi> raspberryInfo;
    private RaspberryPi raspberryPi;
    private View customView;
    private ImageView imageStatus;
    private ViewGroup viewGroup;

    public RPiAdapter(Context context, List<RaspberryPi> raspberryInfo) {
        super(context, R.layout.row_raspberry_details, raspberryInfo);
        this.raspberryInfo = raspberryInfo;
        raspberryPi = raspberryInfo.get(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        customView = layoutInflater.inflate(R.layout.row_raspberry_details, parent, false);
        viewGroup = parent;

//        TextView raspberryName = (TextView) customView.findViewById(R.id.txtRaspberryName);
        TextView raspberryDescription = (TextView) customView.findViewById(R.id.txtRaspberryDescription);
        TextView raspberryId = (TextView) customView.findViewById(R.id.txtRaspberryId);
        TextView raspberryStatus = (TextView) customView.findViewById(R.id.txtRaspberryStatus);
        ImageView imageRaspberry = (ImageView) customView.findViewById(R.id.imgRaspberry);
        imageStatus = (ImageView) customView.findViewById(R.id.imgStatus);

//        raspberryName.setText(raspberryPi.getName());
        raspberryId.setText(raspberryPi.getId());

        if (raspberryPi.getStatus())
            imageStatus.setImageResource(R.drawable.btnon);
        else
            imageStatus.setImageResource(R.drawable.btnoff);

        return customView;
    }


}
