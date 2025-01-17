/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluetooth.comp529.bluetoothchatproj.chat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bluetooth.comp529.bluetoothchatproj.common.logger.Log;
import com.bluetooth.comp529.bluetoothchatproj.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {

    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;
    
    /**
     * all bonded paired devices
     */
    private Set<BluetoothDevice> pairedDevices;
    
    /**
     * all discoverable paired devices
     */
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private Map<String, String> pairedDevicesMap = new HashMap<String, String>();;
    /**
     * Newly discovered devices, saved in adapter
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
    /**
     * discovered devices, save in devicesList
     */
    private Set<String> mNewDevicesSet = new HashSet<String>();
    private Map<String, String> discoverableDevicesMap = new HashMap<String, String>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);
        
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);
        
        
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        pairedDevicesMap = new HashMap<String, String>();
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Get a set of currently paired devices
        pairedDevices = mBtAdapter.getBondedDevices();

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		doDiscovery();
            		TimeUnit.MILLISECONDS.sleep(200);
            	}catch (InterruptedException e) {
            	    //Handle exception
            	}            	
                v.setVisibility(View.GONE);
            }
        });

        
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // If there are paired devices, add each one to the ArrayAdapter
        refreshPairAdapter();
       
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDistroy()");
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.cancelDiscovery();
        mBtAdapter.startDiscovery();
    }
    
    /**
     * refresh the pairDevice Adapter
     */
    private void refreshPairAdapter(){
    	pairedDevicesArrayAdapter.clear();
    	 if (pairedDevices.size() > 0) {
             findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
             for (BluetoothDevice device : pairedDevices) {
             	Log.d(TAG, device.getAddress());
             	for(String deviceName: mNewDevicesSet){
             		Log.d(TAG, deviceName);
             	}
             	//only show those devices are both paired and discoverable:
             	if (mNewDevicesSet.contains(device.getAddress())){
                     pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                     pairedDevicesMap.put(device.getAddress(), device.getName());
             	}  
//                 pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
             } 
         } else {
             String noDevices = getResources().getText(R.string.none_paired).toString();
             pairedDevicesArrayAdapter.add(noDevices);
         }
    	 if (pairedDevicesArrayAdapter.getCount() == 0){
    		 String noDevices = getResources().getText(R.string.none_paired).toString();
    		 pairedDevicesArrayAdapter.add("All paired devices are not discoverable");
    	 }
    	 
    }
    
    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            Log.d(TAG, info);
            if (info.equals("No devices found")){// this string is system default set I guess
            	finish();
            }
            else if (info.equals("No devices have been paired")){// this string is system default set I guess
            	finish();
            }
            else if (info.equals("All paired devices are not discoverable")){
            	finish();
            }
            else{
            	String address = info.substring(info.length() - 17);

            	// Create the result Intent and include the MAC address
            	Intent intent = new Intent();
            	intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            	// Set result and finish this Activity
            	setResult(Activity.RESULT_OK, intent);
            	finish();
            }
        }
    };

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                // well, let's disable this for our case.
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                }
                mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                Log.d(TAG, device.getAddress());
                mNewDevicesSet.add(device.getAddress());
                discoverableDevicesMap.put(device.getAddress(), device.getName());
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        refreshPairAdapter();
        }
        
    };

}
