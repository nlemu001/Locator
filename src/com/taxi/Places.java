package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Places extends Activity{
	ProgressDialog pd;
	Integer UID;
	String uname;
	String nname;
	String phonenum;
	Context context;
	
	Member currentMember;
	ArrayList<Place> places = new ArrayList<Place>();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);
		context = this;
		currentMember = ((Member) this.getApplication());
		try{
			new GetDisplayDataTask (currentMember.getID ()).execute ().get ();
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
        
        ListView listview = (ListView)findViewById(R.id.placesList);
        
        listview.setAdapter(null);
        places = currentMember.getPlaces();
        PlaceRowAdapter adapter = new PlaceRowAdapter(this, places);
        listview.setAdapter(adapter);
	}

	class GetDisplayDataTask extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser ();
		private ProgressDialog progressDialog = new ProgressDialog (context);
		JSONArray Data;

		private String UID = null;

		GetDisplayDataTask (Integer id){
			this.UID = String.valueOf (id);
		}

		protected void onPreExecute () {
			Log.d ("GetDisplayTask", "onPreExecute");
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
			Log.d ("GetDisplayTask", "doInBackground");
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
			}
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			return null;
		}

		protected void onPostExecute (Void v) {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			Log.d ("GetDisplayTask", "onPostExecute");
		}
	}
}
