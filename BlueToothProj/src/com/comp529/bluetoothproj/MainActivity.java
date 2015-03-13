package com.comp529.bluetoothproj;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private static final int REQUEST_ENABLE_BT = 1;
	
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get the default bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		activatingBlueTooth(mBluetoothAdapter);
		
		
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
		}
		
		else if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
}
