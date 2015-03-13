package com.comp529.bluetoothproj.adapter;

import java.util.ArrayList;

import com.comp529.bluetoothproj.R;
import com.comp529.bluetoothproj.model.Pair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PairAdapter extends ArrayAdapter<Pair>{

	public PairAdapter(Context context, ArrayList<Pair> pairs) {
		super(context, 0, pairs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Pair pair = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pair, parent, false);
       }
       // Lookup view for data population
       TextView pairNameTV = (TextView) convertView.findViewById(R.id.pair_name);
       TextView pairAddressTV = (TextView) convertView.findViewById(R.id.pair_address);
       // Populate the data into the template view using the data object
       pairNameTV.setText(String.valueOf(pair.getDeviceName()));
       pairAddressTV.setText(pair.getDeviceAddress());
       
       // Return the completed view to render on screen
       return convertView;
   }

}
