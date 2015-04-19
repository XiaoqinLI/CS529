package com.example.bluetoothnetwork;

import java.util.List;

import com.example.bluetoothnetwork.util.Message;
import com.example.bluetoothnetwork.util.MessageListAdapter;
import com.example.bluetoothnetwork.util.User;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	public static String tag = "com.example.bluetoothnetwork.send_message";
	public static User thisUser = new User("me", 65535);
	Button sendButton;
	EditText sendingMessage;
	MessageListAdapter adapter;
	private List<Message> messageList;
	private int sender;
	private User senderUser;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int sendFrom = intent.getIntExtra("sender", Integer.MAX_VALUE);
			if(sendFrom == sender) adapter.notifyDataSetChanged();
		}
	};
	
	//TODO  and use local broadcast,

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		sender = intent.getIntExtra("sender", Integer.MAX_VALUE);
		senderUser = MessageService.users.get(sender);
//		Log.i("sender is", sender);
		ActionBar actionBar = getActionBar();
		actionBar.setLogo(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		actionBar.setTitle("Conversation with " + senderUser.getUserName());
		actionBar.setDisplayHomeAsUpEnabled(true);
		messageList = MessageService.userMessage.get(senderUser);
		if(messageList == null) Log.i("shit happens", "shit happens");
		// set button and editText
		sendButton = (Button) findViewById(R.id.btnSend);
		sendingMessage = (EditText) findViewById(R.id.inputMsg);
		// button onClick listener
		sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String sendingText = sendingMessage.getText().toString();
				messageList.add(new Message(MainActivity.thisUser, senderUser, sendingText));
				adapter.notifyDataSetChanged();
				Intent intent = new Intent(tag);
				intent.putExtra("sender", sender);
				intent.putExtra("message", sendingText);
				sendingMessage.setText("");
				sendBroadcast(intent);
			}
		});
		
		// deal with list view
		ListView listview = (ListView) findViewById(R.id.list_view_messages);
		adapter = new MessageListAdapter(this, messageList);
		listview.setAdapter(adapter);
	}
	@Override
	protected void onResume() {
		super.onResume();
		// listen to service
		registerReceiver(
						broadcastReceiver, new IntentFilter(MessageService.tag));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
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
		if (id == android.R.id.home) {
	        onBackPressed();
	        return true;
	    }
		return super.onOptionsItemSelected(item);
	}

}
