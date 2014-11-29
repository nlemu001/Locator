package com.taxi;

import java.util.List;

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
	protected void onCreate(Bundle savedInstanceState) {
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
		// TODO Auto-generated method stub
		switch(v.getId()) 
		{
			case R.id.createNewACCOUNT:
			{
				ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
				query.findInBackground(new FindCallback<ParseObject>() 
				{
				    public void done(List<ParseObject> usersList, ParseException e) 
				    {
				        if (e != null)
				        {
				        	//ERROR
				        	Log.d("score", "Error: " + e.getMessage());
				        }
//				        for(int i = 0; i < usersList.size(); ++i)
//				        {
//				        	if(userID < usersList.get(i).getInt("uid"));
//				        		userID = usersList.get(i).getInt("uid") + 1;
//				        }
				        userID = usersList.size() + 1; //THIS IS WRONG IF WE EVER CAN REMOVE A USER
				        
				        ParseObject newUser = new ParseObject("users");
						newUser.put("username", newUsernameField.getText().toString());
						newUser.put("nickname", newNicknameField.getText().toString());
						newUser.put("password", newPasswordField.getText().toString());
						newUser.put("phone", newPhonenumField.getText().toString());
						newUser.put("uid", userID);
						newUser.saveInBackground();
				    }
				    
				});
	
			}
		}
		processAfterNewAccountCreated();
	}
}	