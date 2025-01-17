package com.example.bluetoothnetwork.util;

import java.util.List;

import com.example.bluetoothnetwork.MainActivity;
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
 * The list Adapter that used to maintain and
 * display messages of each paired connected devices
 */
public class MessageListAdapter extends BaseAdapter {
	 
    private Context context;
    private List<Message> messagesItems;
 
    public MessageListAdapter(Context context, List<Message> messagesItems) {
        this.context = context;
        this.messagesItems = messagesItems;
    }
 
    @Override
    public int getCount() {
        return messagesItems.size();
    }
 
    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */
 
        Message m = messagesItems.get(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
 
        // Identifying the message owner
        if (messagesItems.get(position).getSender().equals(MainActivity.thisUser)) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }
 
        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
 
        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getSender().getUserName());
 
        return convertView;
    }
}
