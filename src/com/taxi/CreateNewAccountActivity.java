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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class CreateNewAccountActivity extends Activity implements OnClickListener
{	
	Button createNewAccount;
	EditText newUsernameField;
	EditText newNicknameField;
	EditText newPasswordField;
	EditText newPhonenumField;
	
	View focusView = null;
	Boolean cancel = false;
	Boolean dbcontentdoesntmatch = false;

	Member currentMember = new Member();
	//final Member currentMember = (Member) getApplicationContext();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
		
		
		
		createNewAccount = (Button) findViewById(R.id.createNewACCOUNT);
		
		newUsernameField = (EditText) findViewById(R.id.newUsernameFIELD);
		newNicknameField = (EditText) findViewById(R.id.newNicknameFIELD);
		newPasswordField = (EditText) findViewById(R.id.newPasswordFIELD);
		newPhonenumField = (EditText) findViewById(R.id.newPhonenumFIELD);
		createNewAccount.setOnClickListener(this);
	}
	
	public void processAfterNewAccountCreated()
	{
		
		((Member) this.getApplication()).nickname = newNicknameField.getText().toString();
		((Member) this.getApplication()).email = newUsernameField.getText().toString();
		((Member) this.getApplication()).password = newPasswordField.getText().toString();
		((Member) this.getApplication()).phone_num = newPhonenumField.getText().toString();
		
		final Context context = this;
		Intent newMemberLogin = new Intent(context, MainActivity.class);
		startActivity(newMemberLogin);
		finish();
	}
	
	class CreateUserTask extends AsyncTask<String, String, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(CreateNewAccountActivity.this);
		private InputStream is = null ;
		private String result = "";
		private String username;
		private String nickname;
	    private String password;
	    private String phone;

	    CreateUserTask (String uname, String nick, String pass, String phoneNum) {
	    	username = uname;
	    	nickname = nick;
	    	password = pass;
	    	phone = phoneNum;
	    }

	    protected void onPreExecute() {
	       progressDialog.setMessage("Creating Account");
	       progressDialog.show();
	       progressDialog.setOnCancelListener(new OnCancelListener() {
	    	   @Override
	    	   public void onCancel(DialogInterface arg0) {
	    		   CreateUserTask.this.cancel(true);
	    	   }
	       });
	     }
	      
	    @Override
		protected Void doInBackground(String... params) {
	    	String url_select = "http://rishinaik.com/familyLocator/insert_user.php";
	    	   
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		    param.add (new BasicNameValuePair("username", username));
		    param.add (new BasicNameValuePair("nickname", nickname));
		    param.add (new BasicNameValuePair("password", password));
		    param.add (new BasicNameValuePair("phone", phone));

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(param));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
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
			catch (Exception e) {
					// TODO: handle exception
					Log.e("log_tag", "Error converting result "+e.toString());
			}
			return null;
		}
	    
	    
		protected void onPostExecute(Void v) 	{
			try {
				JSONArray Jarray = new JSONArray(result);
				for(int i=0;i<Jarray.length();i++){
					JSONObject Jasonobject = null;
					Jasonobject = Jarray.getJSONObject(i);
					String retval = Jasonobject.getString("success");
				}
			} 
			catch (Exception e) {
				// TODO: handle exception
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
			this.progressDialog.dismiss();
			//Once the dialogue is dismissed then it means that it was inserted correctly
			//so now we have to create a member object with the parameters and also
			//take the user to the login screen
			processAfterNewAccountCreated();
			
		}
	}
	
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId()) 
		{
			case R.id.createNewACCOUNT:
			{
				new CreateUserTask(newUsernameField.getText().toString(), 
									newNicknameField.getText().toString(), 
									newPasswordField.getText().toString(), 
									newPhonenumField.getText().toString()).execute();
				break;
			}
//			case R.id.newACCOUNT:
//			{
//				//Toast.makeText(getApplicationContext(), "BUTTTTTERRRRRRR", Toast.LENGTH_SHORT);
//				createNewAccount();
//				break;
//			}
		}
		
	}
}

	
	
	
	
	
	
	
//	class task extends AsyncTask<String, String, Void>
//	{
//		private ProgressDialog progressDialog = new ProgressDialog(CreateNewAccountActivity.this);
//		    InputStream is = null ;
//		    String result = "";
//		    protected void onPreExecute() 
//		    {
//		       progressDialog.setMessage("Logging In");
//		       progressDialog.show();
//		       progressDialog.setOnCancelListener(new OnCancelListener() {
//		    	   @Override
//		    	   public void onCancel(DialogInterface arg0) 
//		    	   {
//		    		   task.this.cancel(true);
//		    	   }
//		       });
//		     }
//		      
//		    @Override
//			protected Void doInBackground(String... params) 
//		    {
//				  //String url_select = "http://tekart.byethost9.com/demo.php";
//				  //String url_select = "http://rishinaik.com/stacknotes/demo2.php";
//			      String url_select = "http://rishinaik.com/familyLocator/dbaccess.php";
//			    	   
//			      HttpClient httpClient = new DefaultHttpClient();
//				  HttpPost httpPost = new HttpPost(url_select);
//		
//			      ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
//		
//				  try 
//				  {
//					httpPost.setEntity(new UrlEncodedFormEntity(param));
//		
//					HttpResponse httpResponse = httpClient.execute(httpPost);
//					HttpEntity httpEntity = httpResponse.getEntity();
//		
//					//read content
//					is =  httpEntity.getContent();					
//		
//				  } 
//				  catch (Exception e) 
//				  {
//					Log.e("log_tag", "Error in http connection "+e.toString());
//					//Toast.makeText(MainActivity.this, "Please Try Again", Toast.LENGTH_LONG).show();
//				  }
//				  
//				  try 
//				  {
//				    BufferedReader br = new BufferedReader(new InputStreamReader(is));
//					StringBuilder sb = new StringBuilder();
//					String line = "";
//					while((line=br.readLine())!=null)
//					{
//					   sb.append(line+"\n");
//					}
//					
//					is.close();
//					result=sb.toString();				
//		
//				  } 
//				  catch (Exception e) 
//				  {
//						// TODO: handle exception
//						Log.e("log_tag", "Error converting result "+e.toString());
//				  }
//				  return null;
//	
//			}
//		    
//		    
//			protected void onPostExecute(Void v) 
//			{
//				try 
//				{
//					JSONArray Jarray = new JSONArray(result);
//					for(int i=0;i<Jarray.length();i++)
//					{
//						JSONObject Jasonobject = null;
//
//						Jasonobject = Jarray.getJSONObject(i);
//		
//						String DBusername = Jasonobject.getString("username");
//						String DBpassword = Jasonobject.getString("password");
//
//						
//						if((isEmailValid(usernameField.getText().toString())) && isPasswordValid(passwordField.getText().toString()))
//						{
//							cancel = false;
//						}
//						else
//						{
//							cancel = true;
//						}
//						
//						
//						if(usernameField.getText().toString().equalsIgnoreCase(DBusername) && 
//						   passwordField.getText().toString().equalsIgnoreCase(DBpassword)) 
//						{
//							//login();	
//							break;
//						}
//						else
//						{
//							dbcontentdoesntmatch = true;
//						}
//					}
//				} 
//				catch (Exception e) 
//				{
//					// TODO: handle exception
//					Log.e("log_tag", "Error parsing data "+e.toString());
//				}
//				this.progressDialog.dismiss();
//				if(dbcontentdoesntmatch && isPasswordValid(passwordField.getText().toString()))
//				{
//					passwordField.setError(getString(R.string.error_userpass_incorrect));
//					focusView = passwordField;
//					focusView.requestFocus();
//				}
//				
//			}
//	    }

		