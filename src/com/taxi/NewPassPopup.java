package com.taxi;

import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.taxi.custom.CustomActivity;

// TODO: Auto-generated Javadoc
/**
 * The Activity PaymentPopup is launched when user proceed for the payment of
 * Taxi he/she want to book. Currently it will be launched when user tap on
 * middle button located at the bottom of the Map screen. It will also launched
 * when user click on Reserve button in Search dialog. You must customize and
 * enhance this as per your needs.
 */
public class NewPassPopup extends CustomActivity
{
	EditText mEdit;
	String newPass;
	
	
JSONParser jsonParser = new JSONParser ();
	
	Integer nUID = 0;
	String nNick = "";
	String nPass = "";
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_pass_popup);
		nNick = ((Member) this.getApplication()).nickname;
		nUID = ((Member) this.getApplication()).ID;
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
		View b = findViewById(R.id.btnCancel);
		b.setOnTouchListener(TOUCH);
		b.setOnClickListener(this);

		View b2 = findViewById(R.id.btnAcceptPass);
		mEdit = (EditText)findViewById(R.id.newPass);
		b2.setOnTouchListener(TOUCH);
		b2.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	newPass = mEdit.getText().toString();
		    	
		    	// Insert logic for updating password
		    	
		    	nPass = newPass;
		    	new UpdateUserTask().execute();
		    	
		    	// ----------------------------------
		    }
		});
	}

	
	class UpdateUserTask extends AsyncTask<String, String, String> {

		private ProgressDialog progressDialog = new ProgressDialog(NewPassPopup.this);
		private Integer uid = nUID;
		private String nickname = nNick;
	    private String password = nPass;

	    protected void onPreExecute() {
			progressDialog.setMessage("Updating User");
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
	    	ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
			query.whereEqualTo("uid", uid);
			List<ParseObject> placesList;
			try {
				placesList = query.find();
				placesList.get(0).put("password", password);
				placesList.get(0).save();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
	    	/*
	    	String url = "http://rishinaik.com/familyLocator/update_user.php";
	    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

		    param.add (new BasicNameValuePair("uid", uid));
		    param.add (new BasicNameValuePair("nickname", nickname));
		    param.add (new BasicNameValuePair("password", password));

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
			}*/
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
