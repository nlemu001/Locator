package com.taxi;

import java.io.InputStream;
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

public class NewPhonePopup extends CustomActivity
{
	EditText mEdit;
	String nName;
	JSONParser jsonParser = new JSONParser ();
	
	String nUID = "";
	String nNick = "";
	String nPass = "";
	String pnum  = "";
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_phone_popup);

		WindowManager.LayoutParams wmlp = getWindow().getAttributes();
		wmlp.gravity = Gravity.BOTTOM;
		nPass = ((Member) this.getApplication()).password;
		nUID = ((Member) this.getApplication()).ID.toString();
		nName = ((Member) this.getApplication()).getNickname();
		setupView();
	}

	/**
	 * Setup the click & other events listeners for the view components of this
	 * screen. You can add your logic for Binding the data to TextViews and
	 * other views as per your need.
	 */
	private void setupView()
	{
		View b = findViewById(R.id.btnphoneCancel);
		b.setOnTouchListener(TOUCH);
		b.setOnClickListener(this);

		View b2 = findViewById(R.id.btnAcceptnum);
		mEdit   = (EditText)findViewById(R.id.pnumField);
		b2.setOnTouchListener(TOUCH);
		b2.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	pnum = mEdit.getText().toString();
		    	
		    	// Insert logic for updating nickname
		    	nNick = nName;
		    	new UpdateUserTask().execute();
		    	((Member) getApplication()).setPhoneNumber(pnum);
		    	// ---------------------------------
		    }
		});
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
	
	// new UpdateUserTask(uid, nickname, password).execute();

	class UpdateUserTask extends AsyncTask<String, String, String> {

		private ProgressDialog progressDialog = new ProgressDialog(NewPhonePopup.this);
		private String uid = nUID;
		private String nickname = nNick;
	    private String password = nPass;
	    private String phonenum = pnum;

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
	    	String url = "http://rishinaik.com/familyLocator/update_user.php";
	    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		    param.add (new BasicNameValuePair("uid", uid));
		    param.add (new BasicNameValuePair("nickname", nickname));
		    param.add (new BasicNameValuePair("password", password));
		    param.add (new BasicNameValuePair("phonenum", phonenum));

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
}
