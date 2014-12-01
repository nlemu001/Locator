package com.taxi;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
	Integer count = 0;

	Member currentMember = new Member();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);

		signIn = (Button) findViewById(R.id.signIN);
		newAccount = (Button) findViewById(R.id.newACCOUNT);

		usernameField = (EditText) findViewById(R.id.usernameFIELD);
		passwordField = (EditText) findViewById(R.id.passwordFIELD);

		signIn.setOnClickListener(this);
		newAccount.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent createNewAccountIntent = new Intent(UserLogin.this, CreateNewAccountActivity.class);
				startActivity(createNewAccountIntent);
			}
		});
	}

	public void login()
	{	
		((Member) this.getApplication()).nickname = nicknameFromLogin;
		((Member) this.getApplication()).email = usernameField.getText().toString();
		((Member) this.getApplication()).password = passwordField.getText().toString();
		((Member) this.getApplication()).phone_num = phoneFromLogin;
		((Member) this.getApplication()).ID = idFromLogin;

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	public void createNewAccount()
	{
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

	@Override
	public void onClick(View v) 
	{
		switch(v.getId()) 
		{
		case R.id.signIN:
		{
			if(isEmailValid(usernameField.getText().toString()) && isPasswordValid(passwordField.getText().toString()))
			{
				ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
				query.whereEqualTo("username", usernameField.getText().toString());
				query.whereEqualTo("password", passwordField.getText().toString());
				List<ParseObject> usersList;
				try 
				{
					if(isEmailValid(usernameField.getText().toString()) && isPasswordValid(passwordField.getText().toString()))
					{

						usersList = query.find();
						if(usersList.size() == 0)
						{
							dbcontentdoesntmatch = true;
							passwordField.setError(getString(R.string.error_userpass_incorrect));
							focusView = passwordField;
							focusView.requestFocus();
						}
						else
						{
							nicknameFromLogin = usersList.get(0).getString("nickname");
							phoneFromLogin = usersList.get(0).getString("phone");
							idFromLogin = usersList.get(0).getInt("uid");
							Log.d("USERID", "USERID: " + idFromLogin);
							login();
						}
					}
				} 
				catch (ParseException e1) 
				{
					e1.printStackTrace();
				}

			}
			break;
		}
		}
	}
}
