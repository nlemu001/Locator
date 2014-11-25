package com.taxi;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.telephony.SmsManager;
import android.widget.EditText;

public class MessageActivity extends Activity{
	ProgressDialog pd;
	//EditText txtPhoneNo;
	EditText txtMessage;
	Button button;
	Button button2;
	String contactList1[] = {"6266643618"};
	String contactList2[] = {"6266643618","9512140355","6197262378","8189030157"};
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_activity);
		//txtPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
	    txtMessage = (EditText) findViewById(R.id.editTextSMS);
		addListenerOnButton();
	}
	
	public void addListenerOnButton() 
	{
		final Context context = this;
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  sendSMSMessage();
			}
		});
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  sendSMSMessage2();
			}
		});
 
	}
	
	protected void sendSMSMessage() 
	{
		String phoneNo = "";
	    String message = txtMessage.getText().toString();
	    
		for(int i = 0; i < contactList1.length; i++)
		{
			phoneNo = contactList1[i];
			  try 
			  {
				  SmsManager smsManager = SmsManager.getDefault();
				  smsManager.sendTextMessage(phoneNo, null, message, null, null);
			      Toast.makeText(getApplicationContext(), "SMS sent.",
			      Toast.LENGTH_LONG).show();
			  } 
			  catch (Exception e) {
			      Toast.makeText(getApplicationContext(),
			      "SMS faild, please try again.",
			      Toast.LENGTH_LONG).show();
			      e.printStackTrace();
			  }
		}
	}
	
	protected void sendSMSMessage2() 
	{
		String phoneNo = "";
	    String message = txtMessage.getText().toString();
	    
		for(int i = 0; i < contactList2.length; i++)
		{
			phoneNo = contactList2[i];
			  try 
			  {
				  SmsManager smsManager = SmsManager.getDefault();
				  smsManager.sendTextMessage(phoneNo, null, message, null, null);
			      Toast.makeText(getApplicationContext(), "SMS sent.",
			      Toast.LENGTH_LONG).show();
			  } 
			  catch (Exception e) {
			      Toast.makeText(getApplicationContext(),
			      "SMS faild, please try again.",
			      Toast.LENGTH_LONG).show();
			      e.printStackTrace();
			  }
		}
	}

}