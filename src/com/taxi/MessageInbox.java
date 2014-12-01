package com.taxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MessageInbox extends Activity{
	
	ListView listview;
	Member currentMember;
	ArrayList<String> nicknameList = new ArrayList<String>();
	ArrayList<String> nicknameIDList = new ArrayList<String>();
	Context context;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_message, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.sendNewMessage:
	        	Intent i = new Intent (this, MessageSendActivity.class);
	        	startActivity(i);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_inbox);
		listview = (ListView)findViewById(R.id.messageList);
		currentMember = ((Member) this.getApplication());
		context = this;
		grabMessages();
		
	}
	
	public void grabMessages()
	{
		ParseQuery<ParseObject> fromAdmin = ParseQuery.getQuery("messages");
		fromAdmin.whereEqualTo("from", currentMember.getID());
		 
		ParseQuery<ParseObject> toAdmin = ParseQuery.getQuery("messages");
		toAdmin.whereEqualTo("to", currentMember.getID());
		 
		List<ParseQuery<ParseObject>> adminMessages = new ArrayList<ParseQuery<ParseObject>>();
		adminMessages.add(fromAdmin);
		adminMessages.add(toAdmin);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(adminMessages);
		mainQuery.findInBackground(new FindCallback<ParseObject>() 
		{
		    public void done(List<ParseObject> messageList, ParseException e) 
		    {
		    	Log.d("Userlist", "Error1: " + messageList.size());
		        if (e != null) {
		        	//ERROR
		        	Log.d("Userlist", "Error: " + e.getMessage());
		        }
		        else
		        {
		        	for(int i = 0; i < messageList.size(); ++i)
			        {
		        		if(messageList.get(i).getInt("to") == currentMember.getID())
		        		{
		        			if(!nicknameList.contains(messageList.get(i).getString("fromNickname")))
		        			{
			        			nicknameList.add(messageList.get(i).getString("fromNickname"));
			        			int temp = messageList.get(i).getInt("from");
			        			nicknameIDList.add(Integer.toString(temp));
			        			Log.d("Userlist", "USER: " + messageList.get(i).getInt("from"));
		        			}
		        		}
		        		else
		        		{
		        			if(!nicknameList.contains(messageList.get(i).getString("toNickname")))
		        			{
		        				nicknameList.add(messageList.get(i).getString("toNickname"));
		        				int temp = messageList.get(i).getInt("to");
		        				nicknameIDList.add(Integer.toString(temp));
		        				Log.d("Userlist", "USER: " + messageList.get(i).getInt("to"));
		        			}
		        		}
			        }
		        }
		        showInbox();
			}
		    
		});
	}
	
	public void showInbox()
	{
		//Collections.sort(nicknameList); //used to resort if we decided to do that
		Log.d("Userlist", "Error2: " + nicknameList.size());
		String[] messageNicknames = nicknameList.toArray(new String[0]);
		Log.d("Userlist", "Error3: " + messageNicknames.length);
		ArrayAdapter<String> messageNicknamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messageNicknames);
		listview.setAdapter(messageNicknamesAdapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int pos, long id)
			{
				Log.d("Userlist", "SENDINFO: " + nicknameIDList.get(pos));
				Log.d("Userlist", "SENDINFO2: " + nicknameList.get(pos));
				Intent conversation = new Intent(MessageInbox.this, MessageViewConversation.class);
				conversation.putExtra("conversationID", nicknameIDList.get(pos));
				conversation.putExtra("conversationNickname", nicknameList.get(pos));
				startActivity(conversation);
			}
		});
	}
}
