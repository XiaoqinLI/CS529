package com.example.bluetoothnetwork.util;

import java.util.List;

import com.example.bluetoothnetwork.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * The adapter that organizes all connected devices.
 */
public class ContactListAdapter extends BaseAdapter{
	private Context context;
    private List<User> contactItems;
    public ContactListAdapter(Context context, List<User> contactItems) {
        this.context = context;
        this.contactItems = contactItems;
    }
    @Override
    public int getCount() {
        return contactItems.size();
    }
 
    @Override
    public Object getItem(int position) {
        return contactItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint({ "InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */
 
        User s = contactItems.get(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
 
        // Identifying the message owner

            convertView = mInflater.inflate(R.layout.list_item_contact,
                    null);

 
        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
 
        contactName.setText(s.getUserName());
 
        return convertView;
    }
}
