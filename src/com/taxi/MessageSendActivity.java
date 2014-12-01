package com.taxi;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MessageSendActivity extends Activity{
	ProgressDialog pd;
	//EditText txtPhoneNo;
	EditText txtMessage;
	EditText messageRecipient;
	Button button;
	Button button2;
	Button button3;
	String circleName = "";
	//ArrayList<String> circleNamesList = new ArrayList<String> ();
	String messageString;
	String messageNicknameString;;
	Context context;
	View focusView = null;
	List<ParseObject> usersQueryList = null;
	List<ParseObject> circlesQueryList = null;
	
	Member currentMember;
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_send_activity);
		
	    txtMessage = (EditText) findViewById(R.id.editTextSMS);
	    messageRecipient = (EditText) findViewById(R.id.editTextSMSRecipient);
	    currentMember = ((Member) this.getApplication());
	    context = this;
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) 
	    {
	        //messageString = extras.getString("replyID");
	        //messageNicknameString = extras.getString("replyNickname");
	        circleName = extras.getString("groupName");
	        messageRecipient.setText("All members of: " + circleName);
	    }
	    Log.d("Userlist", "LOOK HERE: ");
		addListenerOnButton();
	}
	
	public void addListenerOnButton() 
	{
		Log.d("Userlist", "LOOK HERE: ");
		final Context context = this;
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!txtMessage.getText().toString().matches(""))
				{
					sendSMSMessage();
				}
				else
				{
					txtMessage.setError(getString(R.string.error_message_is_empty));
					focusView = txtMessage;
					focusView.requestFocus();
				}
			}
		});
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  sendSMSMessage2();
			}
		});
		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  sendSMSMessage3();
			}
		});
 
	}
	
	protected void sendSMSMessage() 
	{	
		ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
		query.whereEqualTo("nickname", messageRecipient.getText().toString());
		query.findInBackground(new FindCallback<ParseObject>() 
		{
		    public void done(List<ParseObject> usersList, ParseException e) 
		    {
		        if (e != null) {
		        	//ERROR
		        	Log.d("Userlist", "Error: " + e.getMessage());
		        }
		        else if(usersList.size() == 0)
		        {
		        	messageRecipient.setError(getString(R.string.error_message_user_doesnt_exist));
					focusView = txtMessage;
					focusView.requestFocus();
		        }
		        else
		        {
		        	ParseObject newMessage = new ParseObject("messages");
		    		newMessage.put("to", usersList.get(0).getInt("uid"));
		    		newMessage.put("toNickname", usersList.get(0).getString("nickname"));
		    		newMessage.put("from", currentMember.getID());
		    		newMessage.put("fromNickname", currentMember.getNickname());
		    		newMessage.put("content", txtMessage.getText().toString());
		    		newMessage.put("mID", 1);
		    		newMessage.saveInBackground();
		    		
		    	Toast toast = Toast.makeText(context, "Message Sent", Toast.LENGTH_SHORT);
				toast.show();
		       }
			}
		});
	}
	
	protected void sendSMSMessage2() 
	{
		Log.d("Userlist", "LOOK HERE: ");
		ParseQuery<ParseObject> queryCircles = ParseQuery.getQuery("circles");
		queryCircles.whereEqualTo("cname",circleName);
		
		ParseQuery<ParseObject> queryUsers = ParseQuery.getQuery("users");
		
		queryUsers.whereMatchesKeyInQuery("uid", "membersID", queryCircles);
		try
		{
			Log.d("Userlist", "LOOK HERE: ");
			usersQueryList = queryUsers.find();
			circlesQueryList = queryCircles.find();
		}
		catch (ParseException e)
		{
			Log.d("Userlist", "LOOK HERE Screw UP: ");
			e.printStackTrace();
		}
		Log.d("Userlist", "LOOK HERE2: " + usersQueryList.size());

		for(int i = 0; i < usersQueryList.size(); ++i)
		{
			Log.d("Userlist", "LOOK HERE2: " + usersQueryList.size());
			ParseObject newMessage = new ParseObject("messages");
			newMessage.put("to", usersQueryList.get(i).getInt("uid"));
			newMessage.put("toNickname", usersQueryList.get(i).getString("nickname"));
			newMessage.put("from", currentMember.getID());
			newMessage.put("fromNickname", currentMember.getNickname());
			newMessage.put("content", txtMessage.getText().toString());
			newMessage.put("mID", 1);
			try {
				newMessage.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	protected void sendSMSMessage3() 
	{
		final Context context = this;
		Intent GoToInbox = new Intent(context, MessageInbox.class);
		startActivity(GoToInbox);
	}

}