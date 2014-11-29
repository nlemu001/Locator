package com.taxi;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.taxi.custom.CustomActivity;

public class NewPlacePopup extends CustomActivity
{
	EditText nameET;
	EditText addressET;
	EditText cityET;
	String nUID = "";
	String placename = "";
	String placeaddress = "";
	String placecity = "";
	Integer ID;
	Geocoder gc = new Geocoder(this);
	Context context;
	Member currentMember;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_place_popup);
		context = this;
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		
		currentMember = ((Member) this.getApplication());
		nUID = ((Member) this.getApplication()).ID.toString();
		ID = ((Member) this.getApplication()).ID;
		WindowManager.LayoutParams wmlp = getWindow().getAttributes();
		wmlp.gravity = Gravity.BOTTOM;

		setupView();
	}

	/**
	 * Setup the click & other events listeners for the view components of this
	 * screen. You can add your logic for Binding the data to TextViews and
	 * other views as per your need.
	 */
	private void setupView()
	{
		nameET = (EditText)findViewById(R.id.newPlaceName);
		addressET = (EditText)findViewById(R.id.placeAddress);
		cityET = (EditText)findViewById(R.id.cityET);
		
		View b = findViewById(R.id.placeBtnCancel);
		b.setOnTouchListener(TOUCH);
		b.setOnClickListener(this);

		View b2 = findViewById(R.id.btnAcceptPlace);
		b2.setOnTouchListener(TOUCH);
		b2.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	placename = nameET.getText().toString();
		    	placeaddress = addressET.getText().toString();
		    	placecity = cityET.getText().toString();
		    	if(placename.equals("") || placeaddress.equals("") || placecity.equals("")){
		    		Toast.makeText(context, "Fill in all fields!", Toast.LENGTH_SHORT).show();
		    		finish();
		    		return;
		    	}
				
				try {
					List<Address> list = gc.getFromLocationName(placeaddress + placecity, 1);
					if(list.size() == 0){
			    		Toast.makeText(context, "Place does not exist!", Toast.LENGTH_SHORT).show();
			    		finish();
			    		return;
			    	}
					Address add = list.get(0);
					Double lat = add.getLatitude();
					Double lng = add.getLongitude();
					Place p = new Place(placename, placeaddress, placecity, ID);
					p.setLat(lat);
					p.setLng(lng);
					currentMember.addPlace(p);
					
					try {
						new UpdatePlaceTask(lat, lng).execute().get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
	}

	class UpdatePlaceTask extends AsyncTask<String, String, String> {

		private ProgressDialog progressDialog = new ProgressDialog(NewPlacePopup.this);
		private String latitude;
		private String longitude;
		
		UpdatePlaceTask(Double lat, Double lng){
			latitude = lat.toString();
			longitude = lng.toString();
		}
		
	    protected void onPreExecute() {
			progressDialog.setMessage("Creating Place");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					UpdatePlaceTask.this.cancel(true);
				}
			});
		}
	      
	    @Override
		protected String doInBackground(String... params) {
	    	
	    	ParseObject newPlace = new ParseObject("places");
	    	newPlace.put("name", placename);
	    	newPlace.put("street", placeaddress);
	    	newPlace.put("city", placecity);
	    	newPlace.put("uid", ID);
	    	newPlace.put("latitude", latitude);
	    	newPlace.put("longitude", longitude);
	    	newPlace.saveInBackground();
	    	
	    	/*
	    	String url = "http://rishinaik.com/familyLocator/insert_place.php";
	    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		    param.add (new BasicNameValuePair("uid", uid));
		    param.add (new BasicNameValuePair("name", placename));
		    param.add (new BasicNameValuePair("street", placeaddress));
		    param.add (new BasicNameValuePair("city", placecity));
		    
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", param);
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
			*/
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
	        }
			finish();
			return null;
		}
	    
	    
		protected void onPostExecute(Void v) 	{
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
	        }
	    }
	}
	
	/* (non-Javadoc)
	 * @see com.taxi.custom.CustomActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		finish();
	}
}
