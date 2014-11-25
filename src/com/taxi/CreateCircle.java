package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateCircle extends Activity 
{
	ProgressDialog pd;
	public EditText circleName;
	EditText members;
	Button button;
	String currentUID;
	String circleName1;
	JSONParser jsonParser = new JSONParser ();
	Context context;
	Member currentMember;
	ArrayList <String> nicknames;
	ListView listview;
	String[] nicknameList;
	ArrayList<String> selectedNames;
	Map<String, String> contacts;
	
	ArrayList<String> keys;
	ArrayList<String> values;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_create);
		currentMember = ((Member) this.getApplication());
		context = this;
		circleName = (EditText)findViewById(R.id.editText1);
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
		Log.d("new circle", "inside create new circle");
		
		String cuid = currentMember.getID().toString();
		for (Map.Entry<String, String> entry : contacts.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if(!key.equals(cuid)){
		    	Log.d("adding mem", key);
		    	keys.add(key);
		    	values.add(value);
		    }
		}
		
		nicknameList = values.toArray(new String[0]);
		
		ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1, nicknameList);
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int pos, long id)
			{
				String itemVal = (String) listview.getItemAtPosition(pos);
				//listview.setItemChecked(pos, true);
				if(selectedNames.indexOf(keys.get(pos)) == -1)
				{
					listview.setItemChecked(pos, true);
					//listview.setBackgroundColor(Color.YELLOW);
					selectedNames.add(keys.get(pos));
				}
				else
				{
					listview.setItemChecked(pos, false);
					//listview.setBackgroundColor(Color.WHITE);
					selectedNames.remove(keys.get(pos));
				}
				Toast.makeText(getApplicationContext(),
						"Position: " + pos + " Name: " + itemVal,
						Toast.LENGTH_SHORT).show();
			}
		});
		
		addListenerOnButton();
		
	}
	
	public void addListenerOnButton()
	{
	
		//final Context context = this;
		
		button = (Button)findViewById(R.id.create_circle_btn);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Create Circle
				for(int j = 0; j < selectedNames.size(); j++)
					Log.d("selected Names", selectedNames.get(j));
				
				currentUID = currentMember.getID().toString();
				circleName1 = circleName.getText().toString();
				Toast.makeText(getApplicationContext(),  circleName1 + " Circle Created", Toast.LENGTH_LONG).show();
				try {
					new CreateDB ().execute().get(2500, TimeUnit.MILLISECONDS);
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
				finish();
			}
		});
	}
	class CreateDB extends AsyncTask<String, String, String> {

		private ProgressDialog progressDialog = new ProgressDialog(context);
		private String uid = currentUID; 
		private String cname = circleName1;

	    protected void onPreExecute() {
			progressDialog.setMessage("Creating Circle");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					CreateDB.this.cancel(true);
				}
			});
		}
	      
	    @Override
		protected String doInBackground(String... params) {
	    	String url_select = "http://rishinaik.com/familyLocator/new_circle.php";
	    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

			for(int i = -1; i < selectedNames.size(); i++)
			{
				if(i == -1)
					param.add (new BasicNameValuePair("nuser", uid));
				else
					param.add (new BasicNameValuePair("nuser", selectedNames.get(i)));
				
				param.add (new BasicNameValuePair("uid", uid));
			    param.add (new BasicNameValuePair("cname", cname));

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
				param.clear();
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
		private ProgressDialog progressDialog = new ProgressDialog (CreateCircle.this);
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
}