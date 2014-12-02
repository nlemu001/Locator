package com.taxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class CircleMembers extends Activity
{
	ListView listview;
	ArrayAdapter<String> adapter;
	String object[] = {""};
	ArrayList<String> circleNonMembers = new ArrayList<String>(Arrays.asList(object)); 
	ArrayList<String> circleMembers = new ArrayList<String>(); 
	Integer removeID = new Integer(999);
	String circleName; //from intent extra
	ArrayList <String> nicknames;
	String nickname = new String();
	ArrayList<String> selectedNames;
	String adminUID;
	String memberID;
	Context context;
	Member currentUser;
	ArrayList<Circle> circlesList = new ArrayList<Circle>();
	ArrayList<String> keys;
	ArrayList<String> values;
	Map<String, String> contacts;
	Circle currentCircle;
	Circle disjCircle;
	String[] nicknameList;
	Boolean dbcontentdoesntmatch = false;
	ArrayList<Integer> membersList = new ArrayList<Integer>();
	ArrayList<Integer> nonMemIDs = new ArrayList<Integer>();
	
	List<ParseObject> circleQueryList = null;
	List<ParseObject> userQueryList	  = null;
	int currCirclePos = 0;
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		startActivity(new Intent(CircleMembers.this, CirclesActivity.class));
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_members);
		
		listview = (ListView)findViewById(R.id.list1);

		currentUser = (Member) this.getApplication();
		String phoneNum = currentUser.phone_num;

		//String[] nicknameList = currentUser.getNicknameArray(currentUser.circle);
		context = this;
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			circleName = extras.getString("cname");
			Log.d("CIRCLENAME", circleName);
		}
		
		//circleName = "Jordan Test";
		
		currentUser = (Member) this.getApplication();
		circlesList = currentUser.getCircleList();
		
		for(int i = 0; i < currentUser.circles.size(); ++i)
		{
			if(currentUser.circles.get(i).getCircleName() == circleName)
			{
				currentCircle = currentUser.circles.get(i);
				
				currCirclePos = i;
				Log.d("CIRCLEPOSITION", getString(currCirclePos));
			}
		}
		
		//Grab all users in current Circle
				ParseQuery<ParseObject> queryUsers = ParseQuery.getQuery("users");
				//queryUsers.whereNotEqualTo("membersID", currentUser.getID());
				//query.whereEqualTo("password", passwordField.getText().toString());
				
				//only take circle with user as admin and matching name (to handle multiple circles of same name)
				ParseQuery<ParseObject> queryCircles = ParseQuery.getQuery("circles");
				queryCircles.whereEqualTo("cname", circleName);
				//queryCircles.whereEqualTo("membersID", currentUser.getID());
				//queryCircles.whereNotEqualTo("membersID", currentUser.getID());
				queryUsers.whereMatchesKeyInQuery("uid", "membersID", queryCircles);
				

				try
				{
					circleQueryList = queryCircles.find();
					userQueryList   = queryUsers.find();
				}
				catch(ParseException e)
				{
					e.printStackTrace();
				}
				
				for(int i = 0; i < userQueryList.size(); ++i)
				{
					String check = userQueryList.get(i).getString("nickname");
					if(check.equals((String) "")) 
					{
						userQueryList.remove(i);
						--i;
					}
					else if(check.equals((String) null))
					{	
						userQueryList.remove(i);
						--i;
					}
					else if (check.equals((String) " "))
					{
						userQueryList.remove(i);
						--i;
					}
					if(i >= 0)
					{
						circleMembers.add(userQueryList.get(i).getString("nickname"));
						membersList.add(new Integer(userQueryList.get(i).getInt("membersID")));

					}
				}
				
				for(int i = 0; i < circleMembers.size(); ++i)
				{
					circleMembers.remove((String)"");
					circleMembers.remove((String)" ");
					circleMembers.remove((String) null);
				}
				
		
		//Log.d("member", "len =  " + nicknames.length);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, circleMembers);

		listview.setAdapter(adapter);


		//final ListView flistview = listview;
		listview.setOnItemClickListener(new OnItemClickListener() 
		{	
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg)
			{
				int itemPos = position;
				currentUser.user = position;
				String itemVal = (String) listview.getItemAtPosition(itemPos);
				currentUser.userNN = itemVal;
//				Toast.makeText(getApplicationContext(),
//						"Position: " + itemPos + " Name: " + itemVal,
//						Toast.LENGTH_SHORT).show();
				finish();

			}
		}); 


		listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{

			public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, final long id)
			{
				int itemPosLongClick = pos;
				currentUser.user = pos;
				String itemValLongClick = (String) listview.getItemAtPosition(itemPosLongClick);
				final String itemValLongClickMessage = (String) listview.getItemAtPosition(itemPosLongClick);
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
						else if(choice == 1)
						{
							Intent intent = new Intent(context, MessageSendActivity.class);
							intent.putExtra("memberName", itemValLongClickMessage);
							intent.putExtra("groupName", " ");
							startActivity(intent);
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