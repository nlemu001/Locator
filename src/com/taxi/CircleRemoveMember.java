package com.taxi;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CircleRemoveMember extends Activity
{
	private ListView listview;
	ArrayAdapter<String> adapter;
	String object[] = {""};
	ArrayList<String> circleMembers = new ArrayList<String>(Arrays.asList(object)); 
	String circleName; //from intent extra
	String adminUID;
	String memberID = new String();
	String currentUID = new String();
	Context context;
	
	Member currentUser;
	Circle currentCircle;
	ArrayList<Circle> circlesList = new ArrayList<Circle>();
	ArrayList<Integer> membersList = new ArrayList<Integer>();
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_remove_member);
		context = this;
		//circleName = 
		//setUpList();
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			circleName = extras.getString("cname");
			Log.d("CIRCLENAME", circleName);
		}

		currentUser = (Member) this.getApplication();
		circlesList = currentUser.getCircleList();
		for(int i = 0; i < circlesList.size(); ++i)
		{
			Circle curr = circlesList.get(i);
			Log.d("Circle Names", curr.getCircleName());;
			if(circleName.equals(curr.getCircleName()))
			{
				currentCircle = curr;
				circleMembers = currentCircle.getNicknames();
				membersList = currentCircle.getCircleMembers();
				Log.d("CURRRRRRRRR", "Found");
			}
		}
		
		int index = circleMembers.indexOf(currentUser.nickname);
		circleMembers.remove(index);
		membersList.remove(index);
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, circleMembers);
		listview = (ListView)findViewById(R.id.list1);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String itemVal = circleMembers.get(position);
				int index = currentCircle.getNicknames().indexOf(itemVal);
				memberID = membersList.get(index).toString();
				Log.d("CN", circleName);
				Log.d("MEM ID", memberID);
				try {
					new RemoveMember(circleName, currentUser.getID(), memberID).execute().get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				circleMembers.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(),
						itemVal + " removed from "+circleName,
						Toast.LENGTH_SHORT).show();	
			}
		});
	};

	class RemoveMember extends AsyncTask<String, String, String> {
		JSONParser jsonParser = new JSONParser ();
		private ProgressDialog progressDialog = new ProgressDialog(context);
		private String uid; 
		private String cname;
		private String member;
		
		RemoveMember (String cn, Integer admin, String mem){
			this.uid = String.valueOf(admin);
			this.cname = cn;
			this.member = mem;
		}
		
		
		protected void onPreExecute() {
			progressDialog.setMessage("Removing member");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					RemoveMember.this.cancel(true);
				}
			});
		}

		@Override
		protected String doInBackground(String... params) {
			String url_select = "http://rishinaik.com/familyLocator/circle_remove_member.php";
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			Log.d("TEST", uid+ "   " + cname + "   " + member);
			
			param.add (new BasicNameValuePair("uid", uid));
			param.add (new BasicNameValuePair("cname", cname));
			param.add (new BasicNameValuePair("member", member));
			JSONObject json = jsonParser.makeHttpRequest(url_select, "POST", param);
			Log.d("Create Response", json.toString());
			try {
				int success = json.getInt("success");
				if (success == 1) {
					Log.d("TASK", "YEAH!");
				} 
				else {
					Log.d("TASK", "OH NO!");
				}
			}
			catch (Exception e) {
				Log.e("log_tag", "Error in http connection "+e.toString());
			}
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			return null;
		}


		protected void onPostExecute(Void v) 	{
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

};