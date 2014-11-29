package com.taxi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
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
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		
		context = this;
		currentMember = ((Member) this.getApplication());
		UID = currentMember.getID();
		try{
			new GetDisplayDataTask (UID).execute ().get ();
		} catch (Exception e) {
			e.printStackTrace();
		}
				
        final Context context = this;
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
