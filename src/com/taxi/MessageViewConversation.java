package com.taxi;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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

public class MessageViewConversation extends Activity{

	ListView listview;
	Context context;
	Member currentMember;
	String conversationString;
	String conversationNicknameString;
	ArrayList<String> conversationList = new ArrayList<String>();
	int conversationID = 0;
	Button reply;
	EditText sendMessage;
	View focusView = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_view_conversation);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		addListenerOnButton();
	    currentMember = ((Member) this.getApplication());
	    sendMessage = (EditText) findViewById(R.id.sendMessage);
	    context = this;
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        conversationString = extras.getString("conversationID");
	        conversationNicknameString = extras.getString("conversationNickname");
	    }
	    setTitle("Conversation with: " + conversationNicknameString);
	    listview = (ListView)findViewById(R.id.conversationList);
		loadConversation();
	}
	
	public void addListenerOnButton()
	{
		reply = (Button) findViewById(R.id.sendButton);
		reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				if(!sendMessage.getText().toString().matches(""))
				{
					replyMessage();
				}
				else
				{
					sendMessage.setError(getString(R.string.error_message_is_empty));
					focusView = sendMessage;
					focusView.requestFocus();
				}
			}
		});
	}
	
	public void loadConversation() 
	{
		conversationID = Integer.parseInt(conversationString);
		
		ParseQuery<ParseObject> fromOtherToAdmin = ParseQuery.getQuery("messages");
		fromOtherToAdmin.whereEqualTo("from", conversationID);
		fromOtherToAdmin.whereEqualTo("to", currentMember.getID());
		 
		ParseQuery<ParseObject> toOtherFromAdmin = ParseQuery.getQuery("messages");
		toOtherFromAdmin.whereEqualTo("to", conversationID);
		toOtherFromAdmin.whereEqualTo("from", currentMember.getID());
		 
		List<ParseQuery<ParseObject>> adminMessages = new ArrayList<ParseQuery<ParseObject>>();
		adminMessages.add(fromOtherToAdmin);
		adminMessages.add(toOtherFromAdmin);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(adminMessages);
		mainQuery.orderByAscending("updatedAt");
		mainQuery.findInBackground(new FindCallback<ParseObject>() 
		{
		    public void done(List<ParseObject> messageList, ParseException e) 
		    {
		    	//Log.d("Userlist", "Error1: " + messageList.size());
		        if (e != null) {
		        	//ERROR
		        	Log.d("Userlist", "Error: " + e.getMessage());
		        }
		        else
		        {
		        	for(int i = 0; i < messageList.size(); ++i)
			        {
		        		conversationList.add(messageList.get(i).getString("content"));
			        }
		        	//Log.d("Userlist", "LOOK: " +conversationList.size());
		        	showConversation();
		        }
			}
		    
		});
	}
	
	public void showConversation()
	{
		String[] conversationContent = conversationList.toArray(new String[0]);
		ArrayAdapter<String> messageConversationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conversationContent);
		listview.setAdapter(messageConversationAdapter);	
	}
	
	public void replyMessage()
	{
    	ParseObject newMessage = new ParseObject("messages");
		newMessage.put("to", conversationID);
		newMessage.put("toNickname", conversationNicknameString);
		newMessage.put("from", currentMember.getID());
		newMessage.put("fromNickname", currentMember.getNickname());
		newMessage.put("content", sendMessage.getText().toString());
		newMessage.put("mID", 1);
		newMessage.saveInBackground();
		
		Toast toast = Toast.makeText(context, "Message Sent", Toast.LENGTH_SHORT);
		toast.show();
			
		Intent goToInbox = new Intent(MessageViewConversation.this, MessageInbox.class);
		startActivity(goToInbox);
		finish();
		/////////////////
		
		
		////////////////
//		Intent replyToMessage = new Intent(MessageViewConversation.this, MessageSendActivity.class);
//		replyToMessage.putExtra("replyID", conversationString);
//		replyToMessage.putExtra("replyNickname", conversationNicknameString);
//		startActivity(replyToMessage);
	}
	
}