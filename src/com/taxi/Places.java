package com.taxi;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Places extends Activity{
	ProgressDialog pd;
	Integer UID;
	String uname;
	String nname;
	String phonenum;
	Context context;
	ListView listview;
	PlaceRowAdapter adapter;
	
	Member currentMember;
	ArrayList<Place> places = new ArrayList<Place>();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);
//		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		
		context = this;
		currentMember = ((Member) this.getApplication());
		UID = currentMember.getID();
		try{
			new GetDisplayDataTask (UID).execute ().get ();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        final Button newplacebtn = (Button) findViewById(R.id.places_newBtn);
        newplacebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(context, NewPlacePopup.class));
            }
        });
        
        listview = (ListView)findViewById(R.id.placesList);
        
        listview.setAdapter(null);
        places = currentMember.getPlaces();
        adapter = new PlaceRowAdapter(this, places);
        listview.setAdapter(adapter);
        longClick();
	}

	protected void longClick(){
		listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{
			public boolean onItemLongClick(final AdapterView<?> adapter, final View view, final int pos, final long id)
			{
				
				final CharSequence[] adminChoices = {"Remove Place"};
				AlertDialog.Builder builder = new AlertDialog.Builder(Places.this);
				builder.setItems(adminChoices, new DialogInterface.OnClickListener() 
				{	
					@Override
					public void onClick(DialogInterface dialog, final int choice) 	
					{							
						if(choice == 0)
						{
							String pname = ((TextView)(view.findViewById(R.id.label))).getText().toString();
							ParseQuery<ParseObject> query = ParseQuery.getQuery("places");
							query.whereEqualTo("uid", UID);
							query.whereEqualTo("name", pname);
							List<ParseObject> placesList;
							try {
								placesList = query.find();								
								places.remove(pos);
								placesList.get(0).deleteInBackground();
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							Toast.makeText(getApplicationContext(), pname + " removed", Toast.LENGTH_SHORT).show();
							((ArrayAdapter<Place>) listview.getAdapter()).notifyDataSetChanged();
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});
	}
	
	class GetDisplayDataTask extends AsyncTask<String, String, String> {
		private ProgressDialog progressDialog = new ProgressDialog (context);
		private Integer UID = null;
		
		GetDisplayDataTask (Integer id){
			this.UID = id;
		}

		protected void onPreExecute () {
			progressDialog.setMessage ("Getting data");
			progressDialog.show ();
			progressDialog.setOnCancelListener (new OnCancelListener () {
				@Override
				public void onCancel (DialogInterface arg0) {
					GetDisplayDataTask.this.cancel (true);
				}
			});
		}       

		@Override
		protected String doInBackground (String... params) {			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("places");
			query.whereEqualTo("uid", UID);
			query.findInBackground(new FindCallback<ParseObject>() 
			{
			public void done(List<ParseObject> placesList, ParseException e) 
			    {
			        if (e != null) {
			            //ERROR
			        	Log.d("score", "Error: " + e.getMessage());
			        }
			        else{
			        	currentMember.clearPlaces();
			        	for (int i = 0; i < placesList.size(); i++) {
							currentMember.addPlace(placesList.get(i).getString("name"), 
												   placesList.get(i).getString("street"), 
												   placesList.get(i).getString("city"));
						}
			        }
			        adapter.notifyDataSetChanged();
			    }
			});
			
			/*
			String url = "http://rishinaik.com/familyLocator/get_places.php";
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair> ();
			
			param.add (new BasicNameValuePair ("uid", UID));
			Log.d ("GetDisplayTask", "UID = " + UID);
			JSONObject json = jParser.makeHttpRequest (url, "POST", param);
			Log.d ("DATA", json.toString ());
			
			try {
				int success = json.getInt ("success");
				if (success == 1) {
					Log.d ("GetDisplayTask", "success");
					Data = json.getJSONArray ("retval");
					currentMember.clearPlaces();
					for (int i = 0; i < Data.length (); i++) {
						JSONObject data = Data.getJSONObject (i);
						currentMember.addPlace(data.getString("name"), data.getString("street"), data.getString("city"));
					}
					Log.d ("GetDisplayTask", "Done");
				} else {
					Log.d ("GetDisplayTask", "OH NO! :(");
				}
			} catch (JSONException e) {
				e.printStackTrace ();
			}*/
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			return null;
		}

		protected void onPostExecute (Void v) {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
		}
	}
}
