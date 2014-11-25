package com.taxi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class CirclesActivity extends Activity 
{
	Member currentUser;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circles_layout);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		final Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CirclesActivity.this, CreateCircle.class);
				startActivity(intent);
				finish();
			}
		});
		
		ListView listview = (ListView)findViewById(R.id.list1);
		currentUser = (Member) this.getApplication();
		
		String[] CircleList = currentUser.getCircleArray ();
		ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, CircleList);
		
		listview.setAdapter(adapter);
		
		final ListView flistview = listview;
		flistview.setOnItemClickListener(new OnItemClickListener() 
		{	
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg)
			{
				int itemPos = position;
				currentUser.circle = position;
				String itemVal = (String) flistview.getItemAtPosition(itemPos);
				Toast.makeText(getApplicationContext(),
						"Position: " + itemPos + " Name: " + itemVal,
						Toast.LENGTH_SHORT).show();
				currentUser.user = -1;
				Intent intent = new Intent(CirclesActivity.this, CircleMembers.class);
				startActivity (intent);
				finish();
			}
		});
		
		flistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, final long id)
			{
				Toast.makeText(getApplicationContext(), "Only the Circle Admin can add/remove users", Toast.LENGTH_SHORT).show();

				
				final CharSequence[] adminChoices = 
					{
						"Add Contact to Circle",
						"Remove Contact from Circle",
						"Share My Location",
						"Do NOT Share My Location"
					};
				
				ArrayList <Circle> userCircles = currentUser.getCircleList();
				final Circle currentCircle = userCircles.get(pos);
				Log.d("CIRCLE ADMIN", currentUser.ID.toString() + "     "+currentCircle.getCircleAdmin());
				AlertDialog.Builder builder = new AlertDialog.Builder(CirclesActivity.this);
				final int position = pos;
				builder.setItems(adminChoices, new DialogInterface.OnClickListener() 
				{	
					@Override
					public void onClick(DialogInterface dialog, int choice) 	
					{
						if(currentCircle.getCircleAdmin() == currentUser.ID)
						{
							Log.d("ADMIN", "user is admin");
							
							if(choice == 0)
							{
								Log.d("POS 0", "IN IF");
								Intent intent = new Intent(CirclesActivity.this, CircleAddMember.class);
								intent.putExtra("cname", currentCircle.getCircleName());
								startActivity (intent);
							}
							else  if(choice == 1)
							{
								Log.d("POS 1", "IN IF");
								Intent intent = new Intent(CirclesActivity.this, CircleRemoveMember.class);
								intent.putExtra("cname", currentCircle.getCircleName());
								startActivity (intent);
							}
						}
						if(choice == 2)
						{
							Toast.makeText(getApplicationContext(), adminChoices[choice] +" with "+currentCircle.getCircleName(), Toast.LENGTH_SHORT).show();
						}
						else if(choice == 3)
						{
							Toast.makeText(getApplicationContext(), adminChoices[choice]+" with "+ currentCircle.getCircleName(), Toast.LENGTH_SHORT).show();
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
