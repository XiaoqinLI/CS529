package com.comp529.bluetoothproj;

import java.util.ArrayList;
import java.util.Set;

import com.comp529.bluetoothproj.adapter.PairAdapter;
import com.comp529.bluetoothproj.model.Pair;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private static final String TAG = "MainActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	private static ArrayList<Pair> arrayOfPairs = new ArrayList<Pair>();
	
	PairAdapter pairAdapter;
	BluetoothAdapter mBluetoothAdapter;
	BroadcastReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		arrayOfPairs.clear();
		pairAdapter = new PairAdapter(this, arrayOfPairs);
		// add a fake testing data
		pairAdapter.add(new Pair("foo", "woo"));
		
		final ListView pairListView = getListView();
		
		pairListView.setAdapter(pairAdapter);
		
		LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.item_pair, pairListView, false);
		
		// get the default bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// turn on BlueTooth, if not supported, then shut the activity down.
		activatingBlueTooth(mBluetoothAdapter);
		// check if there are devices already paired and known.
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	pairAdapter.add(new Pair(device.getName() , device.getAddress()));
		    }
		}
		
		/**
		 * I guess we check connectable devices every 30 seconds or 15 seconds??
		 */
		
		// Enabling discoverability
		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivity(discoverableIntent);
		
		//To start discovering devices, simply call startDiscovery(). The process is asynchronous 
		//and the method will immediately return with a boolean indicating whether discovery has successfully started.
		mBluetoothAdapter.startDiscovery();
		
		// Create a BroadcastReceiver for ACTION_FOUND
		mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            // Place holder, should check if it is already in the set.
		            pairAdapter.add(new Pair(device.getName() , device.getAddress()));
		        }
		    }
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		
		/* how are we gonna do this?
		 * Caution: Performing device discovery is a heavy procedure for the Bluetooth adapter 
		 * and will consume a lot of its resources. Once you have found a device to connect,
		 * be certain that you always stop discovery with cancelDiscovery() before attempting
		 * a connection. Also, if you already hold a connection with a device, then performing
		 * discovery can significantly reduce the bandwidth available for the connection, 
		 * so you should not perform discovery while connected.
		 * */
		

				
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void activatingBlueTooth(BluetoothAdapter mBluetoothAdapter){
		
		if (mBluetoothAdapter == null) {
			Toast.makeText(MainActivity.this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			this.finish();
		}
		
		else if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "started the main activity");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the main activity");
		super.onRestart();	
		// adapter.clear();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the main activity");
		super.onResume();		
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the main activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the main activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the main activity");
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mBluetoothAdapter.cancelDiscovery();
	}
}
