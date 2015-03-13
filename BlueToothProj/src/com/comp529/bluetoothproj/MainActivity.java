package com.comp529.bluetoothproj;

import java.util.ArrayList;
import java.util.Set;

import com.comp529.bluetoothproj.adapter.PairAdapter;
import com.comp529.bluetoothproj.model.Pair;

import android.support.v7.app.ActionBarActivity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static ArrayList<Pair> arrayOfPairs = new ArrayList<Pair>();
	PairAdapter pairAdapter;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pairAdapter = new PairAdapter(this, arrayOfPairs);
		// add a fake testing data
		pairAdapter.add(new Pair("foo", "woo"));
		
		final ListView pairListView = getListView();
		
		pairListView.setAdapter(pairAdapter);
		
		LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.item_pair, pairListView, false);
		
		// get the default bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
}
