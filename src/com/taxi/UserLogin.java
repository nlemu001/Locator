package com.taxi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class UserLogin extends Activity implements OnClickListener
{
	Button signIn;
	Button newAccount;
	EditText usernameField;
	EditText passwordField;
	String nicknameFromLogin;
	Integer idFromLogin;
	String phoneFromLogin;
	View focusView = null;
	Boolean cancel = false;
	Boolean dbcontentdoesntmatch = false;	
	Member currentMember;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);

		signIn = (Button) findViewById(R.id.signIN);
		newAccount = (Button) findViewById(R.id.newACCOUNT);

		usernameField = (EditText) findViewById(R.id.usernameFIELD);
		passwordField = (EditText) findViewById(R.id.passwordFIELD);
		currentMember = ((Member) this.getApplication());
		signIn.setOnClickListener(this);
		newAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{				
				Intent createNewAccountIntent = new Intent(UserLogin.this, CreateNewAccountActivity.class);
				startActivity(createNewAccountIntent);
			}
		});


	}

	public void login () {
		currentMember.setID(idFromLogin);
		currentMember.setNickname (nicknameFromLogin);
		currentMember.setUsername (usernameField.getText ().toString ());
		currentMember.setPassword (passwordField.getText ().toString ());
		currentMember.setPhoneNumber (phoneFromLogin);
		Intent intent = new Intent (this, MainActivity.class);
		startActivity (intent);
		finish ();
	}

	public void createNewAccount () {
		Intent createNewAccountIntent = new Intent(UserLogin.this, CreateNewAccountActivity.class);
		startActivity(createNewAccountIntent);
	}

	public Boolean isEmailValid(String usernameEntry)
	{
		if(usernameEntry.isEmpty()) 
		{
			usernameField.setError(getString(R.string.error_email_field_required));
			focusView = usernameField;
			focusView.requestFocus();
			return false;
		}

		if(usernameEntry.contains("@") && !usernameEntry.contains(" ") && (usernameEntry.contains(".com") || usernameEntry.contains(".edu")))//also .net, .edu, .org
		{
			return true;
		}
		else
		{
			usernameField.setError(getString(R.string.error_incorrect_email));
			focusView = usernameField;
			focusView.requestFocus();
			return false;
		}
	}

	public Boolean isPasswordValid(String password)
	{
		if(password.isEmpty())
		{
			passwordField.setError(getString(R.string.error_password_field_required));
			focusView = passwordField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			return true;
		}
	}

	class LogInTask extends AsyncTask<String, String, Void> {
		private ProgressDialog progressDialog = new ProgressDialog(UserLogin.this);
		InputStream is = null ;
		String result = "";
		protected void onPreExecute() {
			progressDialog.setMessage("Logging In");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) 
				{
					LogInTask.this.cancel(true);
				}
			});
		}

		@Override
		protected Void doInBackground(String... params) {
			String url_select = "http://rishinaik.com/familyLocator/dbaccess.php";

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(param));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				//read content
				is =  httpEntity.getContent();					

			} 
			catch (Exception e) {
				Log.e("log_tag", "Error in http connection "+e.toString());
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while((line=br.readLine())!=null) {
					sb.append(line+"\n");
				}

				is.close();
				result=sb.toString();				

			}
			catch (Exception e) 
			{
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			return null;
		}


		protected void onPostExecute(Void v) {
			try  {
				JSONArray Jarray = new JSONArray(result);
				for(int i=0;i<Jarray.length();i++) {
					JSONObject Jasonobject = null;
					Jasonobject = Jarray.getJSONObject(i);

					String DBusername = Jasonobject.getString("username");
					String DBpassword = Jasonobject.getString("password");
					idFromLogin = Integer.parseInt(Jasonobject.getString("uid"));
					nicknameFromLogin = Jasonobject.getString("nickname");
					phoneFromLogin = Jasonobject.getString("phone");

					if((isEmailValid(usernameField.getText().toString())) && isPasswordValid(passwordField.getText().toString())) {
						cancel = false;
					} else {
						cancel = true;
					}


					if(usernameField.getText().toString().equalsIgnoreCase(DBusername) && 
							passwordField.getText().toString().equalsIgnoreCase(DBpassword)) 
					{
						dbcontentdoesntmatch = false;
						login();	
						break;
					} else {
						dbcontentdoesntmatch = true;
					}
				}
			} 
			catch (Exception e) {
				Log.e("log_tag", "Error parsing data "+e.toString());
			}

			this.progressDialog.dismiss();
			if(dbcontentdoesntmatch && isPasswordValid(passwordField.getText().toString())) {
				passwordField.setError(getString(R.string.error_userpass_incorrect));
				focusView = passwordField;
				focusView.requestFocus();
			}	
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId()) 
		{
		case R.id.signIN:
		{
			try {
				new LogInTask().execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		}

	}
}
