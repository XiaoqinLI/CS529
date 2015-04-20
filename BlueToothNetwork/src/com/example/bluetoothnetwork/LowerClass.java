package com.example.bluetoothnetwork;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.bluetoothnetwork.util.Data;
import com.example.bluetoothnetwork.util.Device;
import com.example.bluetoothnetwork.util.User;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public abstract  class LowerClass extends Service{
	//device to user
	//directedly connected devices
	abstract void onNewNeighbor(User user);
	abstract void onNeighborLeaving(User user);
	abstract void onReceivingData(Data data);
	protected void sendData(String address, byte[] message){
    	// Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            mChatService.write(message, address);
            
        }
    }
	void connectivity(){
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
		//TODO let this run for a while
		//BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		
	} //onNewNeighbor and onNeighborLeaving should be called inside connect(Device device);
	void disconnect(Device device){}
	Set<String> getAllConnectedDevices(){return mChatService.mConnectedThreads.keySet();}
	void dataListener(){} // onReceivingData should be called inside
	
	long macToLong(String mac){
		String[] macAddressParts = mac.trim().split(":");
		long id = 0;
		for(int i = 0; i < 6; i++){
			id*=256;
			id += Long.parseLong(macAddressParts[i], 16);
		}
		return id;
	}
	
	private static final String TAG = "LowerClass";
	public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    
    
    /**
     * discovered devices, save in devicesList
     */
    
    /**
     * Local Bluetooth adapter
     */
    public static BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Member object for the chat services
     */
    protected BluetoothChatService mChatService = null;
    protected void setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(mHandler);
        mChatService.start();
        //Log.d(TAG, "mChatService is Not NULL now");
        
    }
	public static Data deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream b = new ByteArrayInputStream(bytes);
	    ObjectInputStream o = new ObjectInputStream(b);
	    return (Data) o.readObject();
	}
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
				Data receive;
				try {
					receive = deserialize(readBuf);
					// put information from readMessage into Data
					Log.i("LowerClass", "data received");
                    onReceivingData(receive);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
                    
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceAddress = msg.getData().getString("DEVICEADDRESS");
                    User user = new User(msg.getData().getString(Constants.DEVICE_NAME), macToLong(mConnectedDeviceAddress));
                    Log.i("connected", msg.getData().getString(Constants.DEVICE_NAME));
                    onNewNeighbor(user);
                    break;
                case Constants.MESSAGE_LOST:
                	String deviceAddress = msg.getData().getString("DEVICEADDRESS");
                	if(mChatService!=null && mChatService.mConnectedThreads!=null && mChatService.mConnectedThreads.containsKey(deviceAddress)){
	                	mChatService.mConnectedThreads.get(deviceAddress).cancel();
	                	mChatService.mConnectedThreads.remove(deviceAddress);
	                	user = new User("", macToLong(deviceAddress));
	                	onNeighborLeaving(user);
                	}
                	break;

            }
        }
    };
    protected void connectDevice(String address){
    	connectDevice(address, true);
    }
    
    private void connectDevice(String address, boolean secure) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        //Log.i(TAG, "BEGIN connecting to:" + device.getName());
        mChatService.connect(device, secure);
    }
    
    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
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
//                Log.d(TAG, device.getAddress());
                if(device != null && device.getName() != null){
                	Log.i("found sth", device.getName());
                	connectDevice(device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
            
        }
        
    };
   
}
