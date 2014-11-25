package com.taxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.org.apache.http.auth.ChallengeState;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.taxi.JSONParser;
import com.taxi.CreateCircle.CreateDB;
import com.taxi.CreateCircle.GetNamesTask;


public class CircleAddMember extends Activity
{
	private ListView listview;
	ArrayAdapter<String> adapter;
	String object[] = {""};
	ArrayList<String> circleNonMembers = new ArrayList<String>(Arrays.asList(object)); 
	ArrayList<String> circleMembers = new ArrayList<String>(Arrays.asList(object)); 

	String circleName; //from intent extra
	ArrayList <String> nicknames;
	ArrayList<String> selectedNames;
	String adminUID;
	String memberID;
	Context context;
	JSONParser jsonParser = new JSONParser ();
	Member currentUser;
	ArrayList<Circle> circlesList = new ArrayList<Circle>();
	ArrayList<String> keys;
	ArrayList<String> values;
	Map<String, String> contacts;
	Circle currentCircle;
	Circle disjCircle;
	String[] nicknameList;
	ArrayList<Integer> membersList = new ArrayList<Integer>();
	ArrayList<Integer> nonMemIDs = new ArrayList<Integer>();		

		
	public void onCreate(Bundle savedInstanceState)
	{
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_add_member);
		context = this;
		
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			circleName = extras.getString("cname");
			Log.d("CIRCLENAME", circleName);
		}
		
		currentUser = (Member) this.getApplication();
		circlesList = currentUser.getCircleList();
		

		listview = (ListView)findViewById(R.id.usersList);
		nicknames = new ArrayList <String> ();
		selectedNames = new ArrayList <String>();
		contacts = new HashMap<String, String>();
		keys = new ArrayList<String> ();
		values = new ArrayList<String> ();
		
		try {
			new GetNamesTask().execute ().get(2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("ADD USERS", "inside add users to circle");
		
		
		String cuid = currentUser.getID().toString();
		
		Circle selected = new Circle("", 0);
		for(int i = 0; i < circlesList.size(); ++i)
		{
			selected = circlesList.get(i);
			Log.d("Circle Names", selected.getCircleName());;
			if(circleName.equals(selected.getCircleName()))
			{
				currentCircle = selected;
				circleMembers = currentCircle.getNicknames();
				membersList = currentCircle.getCircleMembers();
				Log.d("CURRRRRRRRR", "Found");
			}
		}
		
		for (Map.Entry<String, String> entry : contacts.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if(!key.equals(cuid) && !(circleMembers.contains(value))){
		    	Log.d("adding mem", value + " : " + key);
		    	keys.add(key);
		    	values.add(value);
		    }
		}
		/*
		for(int i = 0; i < circlesList.size(); ++i)
		{

				ArrayList<Integer> currMemIDs = selected.getCircleMembers();
				ArrayList<String> currNicks = selected.getNicknames();
				for(int j =0; j < currMemIDs.size(); ++j)
				{
					//nonMemIDs.add(currMemIDs.get(j));
					if(selected.getNicknames().contains(currNicks.get(j)))
					{
						
					}
					else
					{
						circleNonMembers.add(currNicks.get(j));
						
					}
					if(selected.getCircleMembers().contains(currMemIDs.get(j)))
					{
						Log.d("CURR CIRCLE CONTAINS", currMemIDs.get(j).toString());
					}
					else
					{
						nonMemIDs.add(currMemIDs.get(j));
					}
				}
		}
		
		ArrayList<String> circleNonMembersNew = new ArrayList<String>();
		
		//circleNonMembers = circleNonMembersNew;
		//ArrayList<Integer> nonMemIDsNew = nonMemIDs;
		
		ArrayList<String> currNames = currentCircle.getNicknames();
		ArrayList<Integer> currIDs = currentCircle.getCircleMembers();
		for(int i = 0; i < currIDs.size(); ++i)
		{
			if(values.contains(currNames.get(i)))
			{
				values.remove(values.indexOf(currNames.get(i)));
				
				//Log.d("REMOVE KEY", keys.indexOf(currIDs.get(i)));
			}
			
			if(keys.contains(currIDs.get(i).toString()))
			{
				keys.remove(keys.indexOf(currIDs.get(i)));
				//keys.remove(currIDs.get(i));
				Log.d("REMOVING FROM KEY", currIDs.get(i).toString());
			}
			
		}
		*/
		circleNonMembers = values;
		//nonMemIDs = keys;
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, values);
		listview = (ListView)findViewById(R.id.list1);		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arent, View view, int position, long id)
			{
				String itemVal = values.get(position);
				//int index = 0;
				int index = values.indexOf(itemVal);
				//Log.d("NICKNAMELIST SIZE", nicknameList.length);
				//Log.d("INDEXXXXXXXXXXXXXXXXXXX", index));
				memberID = keys.get(index);
				
				new AddMember().execute();
				
				values.remove(position);
				keys.remove(index);
				adapter.notifyDataSetChanged();
				
				Toast.makeText(getApplicationContext(),
						itemVal + " : " + memberID + " added to " + circleName,
						Toast.LENGTH_SHORT).show();	
			}
		});
	};
	
	

class AddMember extends AsyncTask<String, String, String> {

	private ProgressDialog progressDialog = new ProgressDialog(context);
	private String uid = currentUser.getID().toString(); 
	private String cname = circleName;
	private String member = memberID;
    protected void onPreExecute() {
		progressDialog.setMessage("Adding member");
		progressDialog.show();
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				AddMember.this.cancel(true);
			}
		});
	}
      
    @Override
	protected String doInBackground(String... params) {
    	String url_select = "http://rishinaik.com/familyLocator/circle_add_member.php";
    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url_select);

	    param.add (new BasicNameValuePair("uid", uid));
	    param.add (new BasicNameValuePair("cname", cname));
	    param.add (new BasicNameValuePair("member", memberID));
		JSONObject json = jsonParser.makeHttpRequest(url_select, "POST", param);
		Log.d("Create Response", json.toString());
		try {
			int success = json.getInt("success");
			if (success == 1) {
				Log.d("JSON", "yeah, niga!");
			} 
			else {
				Log.d("JSON", "you're fucked");
			}
		}
		catch (Exception e) {
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
        }
		return null;
	}
    
    
	protected void onPostExecute(Void v) 	{
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
        }
    }
}

class GetNamesTask extends AsyncTask<String, String, String> {
	JSONParser jParser = new JSONParser ();
	private ProgressDialog progressDialog = new ProgressDialog (CircleAddMember.this);
	JSONArray users;
	ArrayList <Integer> list;
	
	GetNamesTask() {

	}
	
    protected void onPreExecute() {
		progressDialog.setMessage("Getting users");
		progressDialog.show();
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				GetNamesTask.this.cancel(true);
			}
		});
	}       

    @Override
	protected String doInBackground(String... params) {
    	String url = "http://rishinaik.com/familyLocator/get_users.php";
    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair> ();
    	
	    JSONObject json = jParser.makeHttpRequest (url, "GET", param);
		Log.d ("All Users: ", json.toString ());

		try {
			int success = json.getInt ("success");
			if (success == 1) {
				users = json.getJSONArray ("users");
				for (int i = 0; i < users.length (); i++) {
					JSONObject user = users.getJSONObject (i);						
					String n = user.getString("nickname");
					String id = user.getString("uid");
					Log.d("member", "adding " + n);
					contacts.put(id, n);
				}
				Log.d("JSON", "yeah, niga!");
			} else {
				Log.d("JSON", "you're fucked");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
        }
		return null;
	}
    
	protected void onPostExecute(Void v) 	{
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
        }
    }
}
	
};