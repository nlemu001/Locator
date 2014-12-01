package com.taxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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

public class parseRemoveMemberFromCircle extends Activity
{
	private ListView listview;
	ArrayAdapter<String> adapter;
	String object[] = {""};
	ArrayList<String> circleNonMembers = new ArrayList<String>(Arrays.asList(object)); 
	ArrayList<String> circleMembers = new ArrayList<String>(Arrays.asList(object)); 
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
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_remove_member);
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
		listview = (ListView)findViewById(R.id.usersList);
		nicknames = new ArrayList <String> ();
		selectedNames = new ArrayList <String>();
		contacts = new HashMap<String, String>();
		keys = new ArrayList<String> ();
		values = new ArrayList<String> ();
		
		//Grab all users in current Circle
		ParseQuery<ParseObject> queryUsers = ParseQuery.getQuery("users");
		queryUsers.whereNotEqualTo("membersID", currentUser.getID());
		//query.whereEqualTo("password", passwordField.getText().toString());
		
		//only take circle with user as admin and matching name (to handle multiple circles of same name)
		ParseQuery<ParseObject> queryCircles = ParseQuery.getQuery("circles");
		queryCircles.whereEqualTo("cname", circleName);
		queryCircles.whereEqualTo("adminID", currentUser.getID());
		queryCircles.whereNotEqualTo("membersID", currentUser.getID());
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
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, circleMembers);
		listview = (ListView)findViewById(R.id.list1);

		listview.setAdapter(adapter);
		//final List <ParseObject> fcircleQueryList = circleQueryList;
		listview.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String itemVal = circleMembers.get(position);
//				//int index = currentCircle.getNicknames().indexOf(itemVal);
				memberID = membersList.get(circleMembers.indexOf(itemVal)).toString();
				//removeID = membersList.get(circleMembers.indexOf(itemVal));
//				Log.d("CN", circleName);
				Log.d("MEM ID", memberID);
				
				ParseObject userRemove = circleQueryList.get(position);
				try 
				{
					userRemove.delete();
					circleQueryList.remove(position);
				} 
				catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				currentUser.circles.get(currCirclePos).deleteMember(membersList.get(circleMembers.indexOf(itemVal)), itemVal);
				
				Integer index = currentUser.contains(circleName, currentUser.getID());
				currentUser.circles.get(index).deleteMember(membersList.get(circleMembers.indexOf(itemVal)), itemVal);
				circleMembers.remove(position);
				membersList.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(),
						itemVal + " removed from "+circleName,
						Toast.LENGTH_SHORT).show();	
			}
		});

		
//		{
//			public void done(List<ParseObject> usersList, ParseException e)
//			{
//				if(e != null)
//				{
//					Log.d("score", "Error: " + e.getMessage());
//				}
//				
//				if(usersList.size() == 0)
//		        {
//		        	Log.d("score", "SADNESS");
//			        dbcontentdoesntmatch = true;
////			        passwordField.setError(getString(R.string.error_userpass_incorrect));
////					focusView = passwordField;
////					focusView.requestFocus();
//		        }
//		        else
//		        {
//		        	for(int i = 0; i < usersList.size(); ++i)
//		        	{		
//			        	Log.d("score", "FOUND");
//			        	nicknames.add(usersList.get(i).getString("nickname"));
//			        	Log.d("ADDED", usersList.get(i).getString("nickname"));
//	//		        	nicknameFromLogin = usersList.get(0).getString("nickname");
//	//		        	phoneFromLogin = usersList.get(0).getString("phone");
//	//		        	idFromLogin = usersList.get(0).getInt("uid");
//	//		        	Log.d("USERID", "USERID: " + idFromLogin);
//		        		dbcontentdoesntmatch = false;
//	//					login();
//		        	}
//		        }
//			}
//		});
	}

}
