package com.taxi;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class CreateNewAccountActivity extends Activity implements OnClickListener
{	
	Button createNewAccount;
	EditText newUsernameField;
	EditText newNicknameField;
	EditText newPasswordField;
	EditText newPhonenumField;
	int userID = 0;
	
	View focusView = null;
	Boolean cancel = false;
	Boolean dbcontentdoesntmatch = false;

	Member currentMember = new Member();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
		
		createNewAccount = (Button) findViewById(R.id.createNewACCOUNT);
		
		newUsernameField = (EditText) findViewById(R.id.newUsernameFIELD);
		newNicknameField = (EditText) findViewById(R.id.newNicknameFIELD);
		newPasswordField = (EditText) findViewById(R.id.newPasswordFIELD);
		newPhonenumField = (EditText) findViewById(R.id.newPhonenumFIELD);
		createNewAccount.setOnClickListener(this);
	}
	
	
	public Boolean credentialsCorrect(String usernameNewAccount, String nicknameNewAccount, String passwordNewAccount, String phoneNumberNewAccount, Boolean uniqueUN)
	{
		Boolean UN = false;
		Boolean NN = false;
		Boolean P = false;
		Boolean PN = false;
		
		//ERROR CHECKS
		//Username (correct form, UNIQUE username, not empty) ----------------------
		if(usernameNewAccount.isEmpty()) 
		{
			newUsernameField.setError(getString(R.string.error_email_field_required));
			focusView = newUsernameField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			UN = true;
		}
		
		if(usernameNewAccount.contains("@") && !usernameNewAccount.contains(" ") && UN && (usernameNewAccount.contains(".com") || usernameNewAccount.contains(".edu")))//also .net, .edu, .org
		{
			UN = true;
		}
		else
		{
			newUsernameField.setError(getString(R.string.error_incorrect_email));
			focusView = newUsernameField;
			focusView.requestFocus();
			return false;
		}
		
		if(!uniqueUN && UN)
		{
    		newUsernameField.setError(getString(R.string.error_username_exists));
			focusView = newUsernameField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			UN = true;
		}
		
		
		//Nickname (less than 12chars, not empty) ----------------------
		if(nicknameNewAccount.isEmpty())
		{
			newNicknameField.setError(getString(R.string.error_nickname_required));
			focusView = newNicknameField;
			focusView.requestFocus();
			return false;
		}
		else if(nicknameNewAccount.length() > 12)
		{
			newNicknameField.setError(getString(R.string.error_nickname_toolong));
			focusView = newNicknameField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			NN = true;
		}
		
		
		//Password (not empty)----------------------
		if(passwordNewAccount.isEmpty()) 
		{
			newPasswordField.setError(getString(R.string.error_password_field_required));
			focusView = newPasswordField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			P = true;
		}
		
		
		//Phone Number (check to see if phone number is valid greater than 1 char less 10, not empty)
		Pattern pattern = Pattern.compile("^[0-9]{10}$");
	    Matcher isPhoneOK = pattern.matcher(phoneNumberNewAccount);
		
		if(phoneNumberNewAccount.isEmpty()) 
		{
			newPhonenumField.setError(getString(R.string.error_phoneNum_field_required));
			focusView = newPhonenumField;
			focusView.requestFocus();
			return false;
		}
		else if (!isPhoneOK.matches()) //are there are no other characters other than numbers
		{
			newPhonenumField.setError(getString(R.string.error_phoneNum_field_incorrect));
			focusView = newPhonenumField;
			focusView.requestFocus();
			return false;
		}
		else
		{
			PN = true;
		}
		
		
		if(UN && NN && P && PN)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public void processAfterNewAccountCreated()
	{
		
		((Member) this.getApplication()).nickname = newNicknameField.getText().toString();
		((Member) this.getApplication()).email = newUsernameField.getText().toString();
		((Member) this.getApplication()).password = newPasswordField.getText().toString();
		((Member) this.getApplication()).phone_num = newPhonenumField.getText().toString();
		((Member) this.getApplication()).ID = userID;
		
		final Context context = this;
		Intent newMemberLogin = new Intent(context, MainActivity.class);
		startActivity(newMemberLogin);
		finish();
	}
	
	public void onClick(View v) 
	{
		Boolean processNewAccount = false;
		
		// TODO Auto-generated method stub
		switch(v.getId()) 
		{
			case R.id.createNewACCOUNT:
			{
				ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
				
				ParseQuery<ParseObject> query2 = ParseQuery.getQuery("users");
				query2.whereEqualTo("username", newUsernameField.getText().toString());
				
                List<ParseObject> usersList;
                List<ParseObject> usersList2;
                try 
                {
                	usersList = query.find();
                    usersList2 = query2.find();
                    userID = usersList.size() + 1; //THIS IS WRONG IF WE EVER CAN REMOVE A USER
                    Integer id = userID;
                    Log.d("creating new user", id.toString());
                    
                    String usernameFieldNewAccount = newUsernameField.getText().toString();
			    	String nicknameFieldNewAccount = newNicknameField.getText().toString();
			    	String passwordFieldNewAccount = newPasswordField.getText().toString();
			    	String phoneNumberFieldNewAccount = newPhonenumField.getText().toString();
			    	Boolean uniqueUN = false;
			    	
			    	if(usersList2.size() == 0)
			        {
			    		uniqueUN = true;
			        }
			    	else
			    	{
						uniqueUN = false;
			    	}
			    	
			    	if(credentialsCorrect(usernameFieldNewAccount, nicknameFieldNewAccount, passwordFieldNewAccount, phoneNumberFieldNewAccount, uniqueUN))
			    	{
	                    ParseObject newUser = new ParseObject("users");
	                    newUser.put("username", newUsernameField.getText().toString());
	                    newUser.put("nickname", newNicknameField.getText().toString());
	                    newUser.put("password", newPasswordField.getText().toString());
	                    newUser.put("phone", newPhonenumField.getText().toString());
	                    newUser.put("uid", userID);
	                    newUser.put("latitude", "0");
	                    newUser.put("longitude", "0");
	                    newUser.save();
	                    processNewAccount = true;
	                    
			    	}
                } 
                catch (ParseException e1) 
                {
                    e1.printStackTrace();
                }
	
			}
		}
		if(processNewAccount)
		{
			processAfterNewAccountCreated();
		}
	}
}	