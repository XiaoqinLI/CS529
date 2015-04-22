package com.example.bluetoothnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.bluetoothnetwork.util.Message;
import com.example.bluetoothnetwork.util.User;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MessageService extends MiddleClass {
	private static final String TAG = "MessageService";
	public static final String tag = "com.example.bluetoothnetwork.new_message";
	public static final String tag_userUpdate = "com.example.bluetoothnetwork.user_update";
	public static List<User> users = new ArrayList<User>();
	public static Set<User> onlineUsers = new HashSet<User>();
	public static Set<User> allUsers = new HashSet<User>();
	public static Map<User, List<Message>> userMessage = new HashMap<User, List<Message>>();
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			;
			sendMessage(users.get(intent.getIntExtra("sender", Integer.MAX_VALUE)), new Message(MainActivity.thisUser, users.get(intent.getIntExtra("sender", Integer.MAX_VALUE)),
					intent.getStringExtra("message")));
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();  
        // Get a set of currently paired devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        String userName = BluetoothAdapter.getDefaultAdapter().getName();
        String add = BluetoothAdapter.getDefaultAdapter().getAddress();
        MainActivity.thisUser = new User(userName, macToLong(add));
		super.init(MainActivity.thisUser);
		registerReceiver(broadcastReceiver, new IntentFilter(MainActivity.tag));
//		BluetoothAdapter.getDefaultAdapter().startDiscovery();
		Log.i(TAG, "I am created!");
		if (mChatService == null) {
        	//Log.d(TAG, "mChatService is NULL");
            setupChat();
        }
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "I am started!");
		// If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        
	}

//	void sendMessage(Message message) {
//		onMessage(new Message(message.getReceiver(), MainActivity.thisUser,
//				message.getMessage()));
//	}
	@Override
	void onNewUser(List<User> updateUsers){
		for(User singleUser : updateUsers){
			
			if(!allUsers.contains(singleUser)){
				allUsers.add(singleUser);
			}
			if(!onlineUsers.contains(singleUser)){
				users.add(singleUser);
				onlineUsers.add(singleUser);
			}
			if(!userMessage.containsKey(singleUser)) 
				userMessage.put(singleUser, new ArrayList<Message>());
		}
		Intent intent = new Intent(tag_userUpdate);
		sendBroadcast(intent);
	}
	@Override
	void onUserDisconnect(List<User> updateUsers){
		for(User singleUser : updateUsers){
			if(onlineUsers.contains(singleUser)){
				onlineUsers.remove(singleUser);
				users.remove(singleUser);
			}
			//TODO we will need this
		}
		Intent intent = new Intent(tag_userUpdate);
		sendBroadcast(intent);
	}
	@Override
	void onMessage(Message message) {
		if (!userMessage.containsKey(message.getSender()))
			userMessage.put(message.getSender(), new ArrayList<Message>());
		userMessage.get(message.getSender()).add(message);
		Intent intent = new Intent(tag);
		intent.putExtra("sender", users.indexOf(message.getSender()));
		sendBroadcast(intent);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		mChatService.stop();
		Log.i(TAG, "I am dead!!!");
		unregisterReceiver(mReceiver);
		unregisterReceiver(broadcastReceiver);
	}
}
