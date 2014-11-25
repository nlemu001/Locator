package com.taxi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CircleMembers extends Activity{

	Member currentUser;


	ArrayList <String> nicknames = new ArrayList <String> ();
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_members);
		
		ListView listview = (ListView)findViewById(R.id.list1);

		currentUser = (Member) this.getApplication();
		String phoneNum = currentUser.phone_num;

		String[] nicknameList = currentUser.getNicknameArray(currentUser.circle);

		Log.d("member", "len =  " + nicknameList.length);
		ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, nicknameList);

		listview.setAdapter(adapter);


		final ListView flistview = listview;
		flistview.setOnItemClickListener(new OnItemClickListener() 
		{	
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg)
			{
				int itemPos = position;
				currentUser.user = position;
				String itemVal = (String) flistview.getItemAtPosition(itemPos);
				Toast.makeText(getApplicationContext(),
						"Position: " + itemPos + " Name: " + itemVal,
						Toast.LENGTH_SHORT).show();

				finish ();
			}
		}); 


		flistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{

			public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, final long id)
			{
				int itemPosLongClick = pos;
				currentUser.user = pos;
				String itemValLongClick = (String) flistview.getItemAtPosition(itemPosLongClick);
				final String phoneNum = currentUser.getPhone(itemValLongClick);
				String displayCallFeature = "Call " + itemValLongClick;
				String displayMessageFeature = "Message " + itemValLongClick;

				final CharSequence[] contactOptions = {displayCallFeature, displayMessageFeature};

				AlertDialog.Builder builder = new AlertDialog.Builder(CircleMembers.this);

				builder.setItems(contactOptions, new DialogInterface.OnClickListener() 
				{	
					@Override
					public void onClick(DialogInterface dialog, int choice) {
						//Call choice 
						if(choice == 0)
						{

							try 
							{
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:"+phoneNum));
								startActivity(callIntent);
							} 
							catch (ActivityNotFoundException activityException) 
							{
								Log.e("Calling a Phone Number", "Call failed", activityException);
							}

						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});
	}
}