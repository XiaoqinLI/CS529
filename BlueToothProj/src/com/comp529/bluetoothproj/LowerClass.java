package com.comp529.bluetoothproj;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;



public abstract class LowerClass {
	/**
	 * device to user
	 * directedly connected devices
	**/
	
//	// Intent request codes
//    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
//    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
//    private static final int REQUEST_ENABLE_BT = 3;
//	
//    private BluetoothChatService mChatService = null;
//    
//	abstract void onNewNeighbor(User user);
//	abstract void onNeighborLeaving(User user);
//	abstract void onReceivingData(Data data);
//	
//	private void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
//        }
//    }
//	
//    private void connectDevice(Device device) {
//        // Get the device MAC address
//        String address = device.getExtras()
//                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        // Get the BluetoothDevice object
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        // Attempt to connect to the device
//        mChatService.connect(device, secure);
//    }
//	
	/*
	sendData(Device device, Data data) {
	}
	connectivity() {
	} //onNewNeighbor and onNeighborLeaving should be called inside
	connect(Device device) {
	}
	disconnect(Device device) {
	}
	getAllConnectedDevices() {
	}
	dataListener() {
	} // onReceivingData should be called inside
	*/
}
