package com.neko642.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by charl on 2016/2/22.
 */
public class RegionAdapter extends ArrayAdapter<Region> {

    private int resourceId;

    public RegionAdapter(Context context,int resourceId,List<Region> regionList){
        super(context,resourceId,regionList);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int positon,View convertView,ViewGroup parent){
        Region region = getItem(positon);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView regionText = (TextView)view.findViewById(R.id.regionText);
        regionText.setText(region.getProvince() + " " + region.getCity() + " " + region.getCounty());
        return view;
    }
}
