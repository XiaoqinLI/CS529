package com.example.bluetoothnetwork;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.bluetoothnetwork.util.ContactListAdapter;
import com.example.bluetoothnetwork.util.User;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * this is the upper level class, which is also the main activity
 * everything start here.
 */
public class ContactActivity extends Activity {
	ContactListAdapter adapter;
	ScheduledExecutorService exec;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BroadcastReceiver userUpdateBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported on this device
        if (mBluetoothAdapter == null) {
            
           this.finish();
        }
        
        ensureDiscoverable();
		LowerClass.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		ListView listview = (ListView) findViewById(R.id.list_view_contacts);
		adapter = new ContactListAdapter(this, MessageService.users);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				User item = (User)adapter.getItem(position);

				Intent intent = new Intent(ContactActivity.this,
						MainActivity.class);
				intent.putExtra("sender",MessageService.users.indexOf(item));
				startActivity(intent);

			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceIntent = new Intent(this, MessageService.class);
		startService(serviceIntent);
		registerReceiver(
				userUpdateBroadcastReceiver, new IntentFilter(MessageService.tag_userUpdate));
		adapter.notifyDataSetChanged();
		if(BluetoothChatService.unConnectable != null) BluetoothChatService.unConnectable.clear();
		//in the begining, wait 5 seconds, and then 
//		mBluetoothAdapter.startDiscovery();
		
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			
			
		  @Override
		  public void run() {
			  
			 // long startTime = System.currentTimeMillis();
			 // long endTime  = startTime + 5000;
			//  while (System.currentTimeMillis() < endTime){
				  mBluetoothAdapter.startDiscovery();
				  try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					Log.e("ContactActivity", "something wrong with thread", e);
					e.printStackTrace();
				}
			 // }
				  
			  mBluetoothAdapter.cancelDiscovery();
			  
		  }
		}, 0, 10, TimeUnit.SECONDS);
		
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		exec.shutdown();
		unregisterReceiver(userUpdateBroadcastReceiver);
		mBluetoothAdapter.cancelDiscovery();
		//Intent serviceIntent = new Intent(this, MessageService.class);
	}
	@Override
	protected void onStart() {
		super.onStart();
		if (!LowerClass.mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, LowerClass.REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
	}
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LowerClass.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    //TODO do someting to kill this app
                }
        }
    }
    protected void ensureDiscoverable() {
    	// check scan mode so that way we do not have to ask again once discoverable.
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
        }
    }
}
