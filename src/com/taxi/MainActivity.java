package com.taxi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.taxi.custom.CustomActivity;
import com.taxi.model.Feed;
import com.taxi.ui.LeftNavAdapter;
import com.taxi.ui.MainFragment;
import com.taxi.ui.RightNavAdapter;

public class MainActivity extends CustomActivity {
	ProgressDialog pd;
	private DrawerLayout drawerLayout;
	private ListView drawerLeft;
	private ListView drawerRight;
	private ActionBarDrawerToggle drawerToggle;
	Integer UID;
	String uname;
	String nname;
	String passw;
	String phone;
	Member currentMember;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		currentMember = (Member) this.getApplication ();
		UID = currentMember.getID ();
		uname = currentMember.getUsername ();
		nname = currentMember.getNickname ();
		passw = currentMember.getPassword ();
		phone = currentMember.getPhoneNumber ();

		setupActionBar ();
		setupDrawer ();
		setupContainer ();
	}

	protected void setupActionBar () {
		final ActionBar actionBar = getActionBar ();
		actionBar.setDisplayShowTitleEnabled (true);
		actionBar.setNavigationMode (ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayUseLogoEnabled (true);
		actionBar.setLogo (R.drawable.theme_black);
		actionBar.setBackgroundDrawable (getResources ().getDrawable(
				R.drawable.action_bar_bg));
		actionBar.setDisplayHomeAsUpEnabled (true);
		actionBar.setHomeButtonEnabled (true);
	}

	private void setupDrawer () {
		drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);
		drawerLayout.setDrawerShadow (R.drawable.drawer_shadow,
				GravityCompat.START);
		drawerToggle = new ActionBarDrawerToggle (this, drawerLayout,
				R.drawable.home_icon, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed (View view) {
				getActionBar ().setTitle (R.string.app_name);
				getActionBar ().setLogo (R.drawable.theme_black);
				invalidateOptionsMenu ();
			}

			@Override
			public void onDrawerOpened (View drawerView) {
				if (drawerView == drawerLeft) {
					getActionBar ().setTitle (R.string.home);
				}
				else {
					getActionBar ().setLogo (R.drawable.ic_chat);
					getActionBar ().setTitle (R.string.online_taxi_driver);
				}
				invalidateOptionsMenu ();
			}
		};
		drawerLayout.setDrawerListener (drawerToggle);
		drawerLayout.closeDrawers ();

		setupLeftNavDrawer ();
		setupRightNavDrawer ();
	}

	@SuppressLint("InflateParams") 
	private void setupLeftNavDrawer() {
		drawerLeft = (ListView) findViewById(R.id.left_drawer);
		View header = getLayoutInflater().inflate(R.layout.left_nav_header, null);

		
		drawerLeft.addHeaderView(header);

		TextView tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setText(nname);

		TextView tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(uname);

		try {
			new DisplayImageFromURL((ImageView) findViewById(R.id.imageViewleftnavheader))
			.execute("https://s3-us-west-1.amazonaws.com/familylocatorprofile/" + UID + ".jpeg")
			.get(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final ArrayList<Feed> al = new ArrayList<Feed>();
		al.add(new Feed("View Profile", null, R.drawable.profile));
		al.add(new Feed("Send Message", null, R.drawable.ic_chat));
		al.add(new Feed("View Circles", null, R.drawable.ic_left5));
		al.add(new Feed("Favorite Places", null, R.drawable.ic_left4));
		//al.add(new Feed("Contacts", null, R.drawable.ic_left5));
		al.add(new Feed("Sign Out", null, R.drawable.ic_left6));

		final LeftNavAdapter adp = new LeftNavAdapter(this, al);
		drawerLeft.setAdapter(adp);
		final Context context = this;
		drawerLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View arg1, int arg2,long arg3)
			{
				if (arg2 == 0)
					return;
				for (Feed f : al)
					f.setDesc(null);
				//al.get(arg2 - 1).setDesc("");
				adp.notifyDataSetChanged();
				// drawerLayout.closeDrawers();

				//This is where I set the view to change to another page
				if(arg2 == 1) //View Profile
				{
					Intent i = new Intent(context, ProfileActivity.class);
					startActivity(i);
				}
				else if(arg2 == 2) //Send Message
				{
					Intent i = new Intent(context, MessageActivity.class);
					startActivity(i);
				}
				else if(arg2 == 3) //Circles
				{
					try 
					{
						new GetDataTask (UID).execute ().get ();
					} 
					catch (Exception e) 
					{
						e.printStackTrace ();
					} 
					Intent i = new Intent(context, CirclesActivity.class);
					startActivity(i);
				}
				else if(arg2 == 4)
				{
					Intent i = new Intent(context, Places.class);
					startActivity(i);
				}
				else if(arg2 == 5) //Sign Out
				{
					Intent i = new Intent(context, UserLogin.class);
					startActivity(i);
					finish();
				}
				drawerLayout.closeDrawer (drawerLeft);
			}
		});

	}

	@SuppressLint ("InflateParams") 
	private void setupRightNavDrawer () {
		drawerRight = (ListView) findViewById (R.id.right_drawer);

		View header = getLayoutInflater ().inflate (R.layout.rigth_nav_header, null);
		drawerRight.addHeaderView (header);

		ArrayList<Feed> al = new ArrayList<Feed> ();
		al.add (new Feed ("Taxi 00689", "touch to chat", R.drawable.img_f1, true));
		al.add (new Feed ("Taxi 00783", "unavailable for chat",
				R.drawable.img_f2, false));
		al.add (new Feed ("Taxi 01632", "unavailable for chat",
				R.drawable.img_f3, false));
		al.add (new Feed ("Taxi 00321", "touch to chat", R.drawable.img_f4, true));

		drawerRight.setAdapter (new RightNavAdapter (this, al));
	}

	private void setupContainer () {
		getFragmentManager ().beginTransaction ()
		.replace (R.id.content_frame, new MainFragment ()).commit ();
	}

	private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(MainActivity.this, "Waiting...", "Please wait five seconds..."); ;
		}
		public DisplayImageFromURL (ImageView bmImage) {
			this.bmImage = bmImage;
		}
		protected Bitmap doInBackground (String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL (urldisplay).openStream ();
				mIcon11 = BitmapFactory.decodeStream (in);
				mIcon11= Bitmap.createScaledBitmap (mIcon11, 100, 100, true);
			} catch (Exception e) {
				Log.e("Error", e.getMessage ());
				e.printStackTrace ();
			}
			return mIcon11;
		}
		protected void onPostExecute (Bitmap result) {
			bmImage.setImageBitmap (result);
			if (pd.isShowing ()) {
				pd.dismiss ();
			}
		}
	}

	private class GetDataTask extends AsyncTask<String, String, String> {
		JSONParser jParser = new JSONParser ();
		private ProgressDialog progressDialog = new ProgressDialog (MainActivity.this);
		JSONArray Data;

		private String UID = null;

		GetDataTask (Integer id){
			this.UID = String.valueOf (id);
		}

		protected void onPreExecute () {
			progressDialog.setMessage ("Getting data");
			progressDialog.show ();
			progressDialog.setOnCancelListener (new OnCancelListener () {
				@Override
				public void onCancel (DialogInterface arg0) {
					GetDataTask.this.cancel (true);
				}
			});
		}       

		@Override
		protected String doInBackground (String... params) {
			String url = "http://rishinaik.com/familyLocator/get_data.php";
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair> ();
			Log.d ("dib", "in finct");
			param.add (new BasicNameValuePair ("uid", UID));

			JSONObject json = jParser.makeHttpRequest (url, "POST", param);
			Log.d ("DATA", json.toString ());
			Log.d ("dib", "in finct  2");
			currentMember.circles.clear ();
			Log.d ("MainAct", "clearing circles");
			try {
				int success = json.getInt ("success");
				if (success == 1) {
					Log.d("DataTask", "success");
					Data = json.getJSONArray ("retval");
					for (int i = 0; i < Data.length (); i++) {
						JSONObject data = Data.getJSONObject (i);
						HashMap<String, String> map = new HashMap<String, String> ();
						Integer admin = data.getInt ("adminID");
						String cname = data.getString ("cname");
						map.put ("uid", data.getString ("uid"));
						map.put ("nickname", data.getString("nickname"));
						map.put ("lat", data.getString("lat"));
						map.put ("lng", data.getString("lng"));
						map.put ("phone", data.getString("phone"));
						int index = currentMember.contains (cname, admin);

						if (index < 0) {
							currentMember.addCircle (new Circle (cname, admin));
							int len = currentMember.circles.size ()-1;
							currentMember.circles.get (len).addMember (admin, map.get ("nickname"), map);
						} else {
							currentMember.circles.get (index).addMember (Integer.valueOf (map.get ("uid")), map.get ("nickname"), map);
						}
					}
					Log.d ("GetDataTask", "done");
				} 
				else {
					Log.e ("GetDataTask", "oh, no!");
				}
			} catch (JSONException e) {
				e.printStackTrace ();
			}
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			Log.d ("dib", "in finct  2");
			return null;
		}
	}

	@Override
	public void onResume (){
		super.onResume ();
		try {
			new DisplayImageFromURL ((ImageView) findViewById (R.id.imageViewleftnavheader))
			.execute ("https://s3-us-west-1.amazonaws.com/familylocatorprofile/" + UID + ".jpeg")
			.get ();
		} catch (Exception e) {
			e.printStackTrace ();
		} 
		TextView tv1 = (TextView) findViewById (R.id.textView1);
		nname = ((Member) this.getApplication ()).nickname;
		tv1.setText (nname);
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate (savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState ();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged (newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged (newConfig);
	}


	@Override
	public boolean onCreateOptionsMenu (Menu menu){
		super.onCreateOptionsMenu (menu);
		getMenuInflater ().inflate (R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		boolean drawerOpen = drawerLayout.isDrawerOpen (drawerRight);
		menu.findItem (R.id.menu_chat).setVisible (!drawerOpen);
		return super.onPrepareOptionsMenu (menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item))
		{
			if (drawerLayout.isDrawerOpen (drawerRight))
				drawerLayout.closeDrawers ();
			return true;
		}
		if (item.getItemId() == R.id.menu_chat)
		{
			drawerLayout.closeDrawer (drawerLeft);
			drawerLayout.openDrawer (drawerRight);
			return true;
		}
		return super.onOptionsItemSelected (item);
	}

	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		Log.e ("KEy", "YEs");
		return super.onKeyDown (keyCode, event);
	}
}
