package com.taxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CreateCircle extends Activity 
{
	ProgressDialog pd;
	public EditText circleName;
	EditText members;
	Button button;
	String currentUID;
	String circleName1;
	//JSONParser jsonParser = new JSONParser ();
	Context context;
	Member currentMember;
	ListView listview;
	String[] nicknameList;
	ArrayList<String> selectedNames;
	//Map<String, String> contacts;
	
	ArrayList<String> keys;
	ArrayList<String> values;
	
	//private ParseQueryAdapter<ParseObject> mainAdapter;
	
	protected void onCreate(Bundle savedInstanceState)
	{
//		Parse.initialize(this, "QjBCQwxoQdR6VtYp2tyrGvQLlf7eKEBzPjAZVcGm", "IbgUMSFPZubtrtj7rJ1wxDAce6lcUuLv4N4GCDCW");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_create);
		currentMember = ((Member) this.getApplication());
		context = this;
		circleName = (EditText)findViewById(R.id.editText1);
		listview = (ListView)findViewById(R.id.usersList);
		selectedNames = new ArrayList <String>();
		//contacts = new HashMap<String, String>();
		keys = new ArrayList<String> ();
		values = new ArrayList<String> ();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
		query.whereNotEqualTo("uid", currentMember.getID());
		query.findInBackground(new FindCallback<ParseObject>() 
		{
		    public void done(List<ParseObject> usersList, ParseException e) 
		    {
		        if (e != null) {
		        }
		        else
		        {
		        	for(int i = 0; i < usersList.size(); ++i)
			        {
		        		String id = String.valueOf(usersList.get(i).getInt("uid"));
			        	String n = usersList.get(i).getString("nickname");

						keys.add(id);
						values.add(n);
			        }
		        	usersToList();
		        }
			}
		});
		addListenerOnButton();
	}
	
	public void usersToList()
	{
		 ParseQueryAdapter.QueryFactory<ParseObject> factory =
			     new ParseQueryAdapter.QueryFactory<ParseObject>() {
			       public ParseQuery create() {
			         ParseQuery query = new ParseQuery("users");
			         query.whereNotEqualTo("uid", currentMember.getID());
			         return query;
			       }
			     };
		ParseQueryAdapter<ParseObject> mainAdapter = new ParseQueryAdapter<ParseObject>(this, factory);
		mainAdapter.setTextKey("nickname");
		
		listview.setAdapter(mainAdapter);
		
		String cuid = currentMember.getID().toString();
	
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int pos, long id)
			{
				if(selectedNames.indexOf(keys.get(pos)) == -1) 
				{
					listview.setItemChecked(pos, true);
					selectedNames.add(keys.get(pos));
				}
				else //found in list already so remove it
				{
					listview.setItemChecked(pos, false);
					selectedNames.remove(keys.get(pos));
					Toast toast = Toast.makeText(context, "Removing " + 
							values.get(pos) + " from " + circleName.getText().toString(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	public void addListenerOnButton()
	{
		button = (Button)findViewById(R.id.create_circle_btn);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				addCircleToDatabase();
				Intent createNewAccountIntent = new Intent(CreateCircle.this, CirclesActivity.class);
				startActivity(createNewAccountIntent);
				finish();
			}
		});
	}
	
	public void addCircleToDatabase()
	{
		for(int i = -1; i < selectedNames.size(); ++i)
		{
			ParseObject newCircle = new ParseObject("circles");
			if(i == -1)
				newCircle.put("membersID", currentMember.getID());//newPasswordField.getText().toString());
			else
				newCircle.put("membersID", Integer.parseInt(selectedNames.get(i)));//newPasswordField.getText().toString());
			
			newCircle.put("adminID",currentMember.getID());
			newCircle.put("cname",  circleName.getText().toString());
			newCircle.put("shareLocation", 0); //Hardcoded so may need to change
			newCircle.saveInBackground();
		}
	}
}