package com.taxi;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.taxi.custom.CustomActivity;

// TODO: Auto-generated Javadoc
/**
 * The Activity PaymentPopup is launched when user proceed for the payment of
 * Taxi he/she want to book. Currently it will be launched when user tap on
 * middle button located at the bottom of the Map screen. It will also launched
 * when user click on Reserve button in Search dialog. You must customize and
 * enhance this as per your needs.
 */
public class NewPlacePopup extends CustomActivity
{
	EditText nameET;
	EditText addressET;
	EditText cityET;
	JSONParser jsonParser = new JSONParser ();
	String nUID = "";
	String placename = "";
	String placeaddress = "";
	String placecity = "";
	Integer ID;
	
	Member currentMember;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_place_popup);
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
		    	
		    	// Insert logic for creating a new place
		    	
		    	new UpdateUserTask().execute();
		    	currentMember.addPlace(placename, placeaddress, placecity);
		    	// ----------------------------------
		    }
		});
	}

	
	class UpdateUserTask extends AsyncTask<String, String, String> {

		private ProgressDialog progressDialog = new ProgressDialog(NewPlacePopup.this);
		private String uid = nUID;

	    protected void onPreExecute() {
			progressDialog.setMessage("Creating Place");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					UpdateUserTask.this.cancel(true);
				}
			});
		}
	      
	    @Override
		protected String doInBackground(String... params) {
	    	// TO-DO create new_place.php
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
