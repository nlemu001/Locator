package com.taxi;

<<<<<<< HEAD
=======
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
>>>>>>> origin/master
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

<<<<<<< HEAD
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

=======
import com.amazonaws.com.fasterxml.jackson.databind.deser.std.JdkDeserializers.CurrencyDeserializer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.taxi.CircleRemoveMember.RemoveMember;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

>>>>>>> origin/master
public class parseRemoveMemberFromCircle extends Activity
{
	private ListView listview;
	ArrayAdapter<String> adapter;
	String object[] = {""};
	ArrayList<String> circleNonMembers = new ArrayList<String>(Arrays.asList(object)); 
	ArrayList<String> circleMembers = new ArrayList<String>(Arrays.asList(object)); 
<<<<<<< HEAD
	Integer removeID = new Integer(999);
=======

>>>>>>> origin/master
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
<<<<<<< HEAD
	int currCirclePos = 0;
=======
>>>>>>> origin/master
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
<<<<<<< HEAD
		setContentView(R.layout.circle_remove_member);
=======
		setContentView(R.layout.circle_add_member);
>>>>>>> origin/master
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
		
<<<<<<< HEAD
		for(int i = 0; i < currentUser.circles.size(); ++i)
		{
			if(currentUser.circles.get(i).getCircleName() == circleName)
			{
				currentCircle = currentUser.circles.get(i);
				
				currCirclePos = i;
				Log.d("CIRCLEPOSITION", getString(currCirclePos));
			}
		}
=======
>>>>>>> origin/master
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
<<<<<<< HEAD
			if(i >= 0)
			{
				circleMembers.add(userQueryList.get(i).getString("nickname"));
				membersList.add(new Integer(userQueryList.get(i).getInt("membersID")));

			}
=======
			circleMembers.add(userQueryList.get(i).getString("nickname"));
			membersList.add(userQueryList.get(i).getInt("membersID"));
>>>>>>> origin/master
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
<<<<<<< HEAD
				//removeID = membersList.get(circleMembers.indexOf(itemVal));
=======
>>>>>>> origin/master
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
<<<<<<< HEAD
			
				currentUser.circles.get(currCirclePos).deleteMember(membersList.get(circleMembers.indexOf(itemVal)), itemVal);
				
				Integer index = currentUser.contains(circleName, currentUser.getID());
				currentUser.circles.get(index).deleteMember(membersList.get(circleMembers.indexOf(itemVal)), itemVal);
=======
>>>>>>> origin/master
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
