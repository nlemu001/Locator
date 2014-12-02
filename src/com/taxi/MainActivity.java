package com.taxi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.taxi.custom.CustomActivity;
import com.taxi.model.Feed;
import com.taxi.ui.LeftNavAdapter;
import com.taxi.ui.MainFragment;

public class MainActivity extends CustomActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

	ProgressDialog pd;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private DrawerLayout drawerLayout;
	private ListView drawerLeft;
	private ActionBarDrawerToggle drawerToggle;
	private Integer UID;
	private String uname;
	private String nname;
	private Member currentMember;
	private LocationClient mLocationClient;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		currentMember = (Member) this.getApplication ();
		UID = currentMember.getID ();
		uname = currentMember.getUsername ();
		nname = currentMember.getNickname ();
		mLocationClient = new LocationClient(this, this, this);
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
				invalidateOptionsMenu ();
			}
		};
		drawerLayout.setDrawerListener (drawerToggle);
		drawerLayout.closeDrawers ();

		setupLeftNavDrawer ();
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
			.get();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		final ArrayList<Feed> al = new ArrayList<Feed>();
		al.add(new Feed("View Profile", null, R.drawable.profile));
		al.add(new Feed("View Inbox", null, R.drawable.ic_chat));
		al.add(new Feed("View Circles", null, R.drawable.ic_left5));
		al.add(new Feed("Favorite Places", null, R.drawable.ic_left4));
		al.add(new Feed("Sign Out", null, R.drawable.ic_left6));
		al.add(new Feed("About", null, R.drawable.ic_left7));
		
		final LeftNavAdapter adp = new LeftNavAdapter(this, al);
		drawerLeft.setAdapter(adp);
		final Context context = this;
		drawerLeft.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View arg1, int arg2,long arg3){
				if (arg2 == 0)
					return;
				for (Feed f : al)
					f.setDesc(null);

				adp.notifyDataSetChanged();

				if(arg2 == 1) {
					Intent i = new Intent(context, ProfileActivity.class);
					startActivity(i);
				}
				else if(arg2 == 2) {
					Intent i = new Intent(context, MessageInbox.class);
					startActivity(i);
				}
				else if(arg2 == 3) {
					try {
						new GetDataTask ().execute ().get ();
					} catch (Exception e) {
						e.printStackTrace ();
					} 
					Intent i = new Intent(context, CirclesActivity.class);
					startActivity(i);
				}
				else if(arg2 == 4){
					Intent i = new Intent(context, Places.class);
					startActivity(i);
				}
				else if(arg2 == 5) {
					Intent i = new Intent(context, UserLogin.class);
					startActivity(i);
					finish();
				}
				else if(arg2 == 6) {
					Intent i = new Intent(context, About.class);
					startActivity(i);
				}
				drawerLayout.closeDrawer (drawerLeft);
			}
		});

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
		private ProgressDialog progressDialog = new ProgressDialog (MainActivity.this);

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

		@SuppressLint("UseSparseArrays") @Override
		protected String doInBackground (String... params) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("circles");
			query.whereEqualTo("membersID", currentMember.getID());
        	ParseQuery<ParseObject> query2 = ParseQuery.getQuery("circles");
        	query2.whereMatchesKeyInQuery("adminID", "adminID", query);
        	query2.whereMatchesKeyInQuery("cname", "cname", query);
        	ParseQuery<ParseObject> query3 = ParseQuery.getQuery("users");
        	query3.whereMatchesKeyInQuery("uid", "membersID", query2);
        	List<ParseObject> result = null;
        	List<ParseObject> result2 = null;
        	try {
        		result = query2.find();
        		result2 = query3.find();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        	HashMap<Integer, HashMap<String, String>> users = new HashMap<Integer, HashMap<String, String>>();
        	for (ParseObject o : result2) {
        		HashMap<String, String> map = new HashMap<String, String>();
        		map.put("uid", String.valueOf(o.getInt ("uid")));
        		map.put("lat", o.getString ("latitude"));
        		map.put("lng", o.getString ("longitude"));
        		map.put("nickname", o.getString ("nickname"));
        		users.put(o.getInt("uid"), map);
        	}
     	
        	for (ParseObject o : result) {
        		Integer admin = o.getInt ("adminID");
        		String cname = o.getString ("cname");
        		int index = currentMember.contains (cname, admin);
        		if (index < 0) {
        			currentMember.addCircle (new Circle (cname, admin));
        			int len = currentMember.circles.size ()-1;
        			currentMember.circles.get (len).addMember(admin, users.get(admin).get("nickname"), users.get(admin));
        		} else {
        			currentMember.circles.get (index).addMember (o.getInt("membersID"), users.get(o.getInt("membersID")).get("nickname"), users.get(o.getInt("membersID")));
        		}
        	}

			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
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
		drawerToggle.syncState ();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged (newConfig);
		drawerToggle.onConfigurationChanged (newConfig);
	}


	@Override
	public boolean onCreateOptionsMenu (Menu menu){
		super.onCreateOptionsMenu (menu);
//		getMenuInflater ().inflate (R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		return super.onPrepareOptionsMenu (menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		return super.onOptionsItemSelected (item);
	}

	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		return super.onKeyDown (keyCode, event);
	}


	@Override
	public void onConnected(Bundle dataBundle) {
		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
		requestLocationUpdates();
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			} 
		}else {
		} 
	}

	private void requestLocationUpdates() {
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(7000);
		request.setFastestInterval(5000);
		mLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onLocationChanged(Location loc) {
		try {
			new UpdateLocationTask (UID, loc.getLatitude(), loc.getLongitude()).execute ().get ();
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentMember.setLat(loc.getLatitude());
		currentMember.setLng(loc.getLongitude());
		currentMember.updateDisplay(UID, loc.getLatitude(), loc.getLongitude());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationClient.removeLocationUpdates(this);
	}

	class UpdateLocationTask extends AsyncTask <String, String, Void> {
		private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
		InputStream is = null ;
		String result = "";
		Integer UID = null;
		String lat = null;
		String lng = null;
		UpdateLocationTask (Integer id, Double latitude, Double longitude){
			this.UID = id;
			this.lat = String.valueOf(latitude);
			this.lng = String.valueOf(longitude);
		}


		protected void onPreExecute() 
		{
			progressDialog.setMessage("Updating Location");
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) 
				{
					UpdateLocationTask.this.cancel(true);
				}
			});
		}

		@Override
		protected Void doInBackground(String... params) 
		{
			ParseQuery<ParseObject> query = ParseQuery.getQuery("users");
			query.whereEqualTo("uid", UID);
			List<ParseObject> placesList;
			try {
				placesList = query.find();
				placesList.get(0).put("latitude", lat);
				placesList.get(0).put("longitude", lng);
				placesList.get(0).save();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
				  
			return null;
		}

		protected void onPostExecute(Void v) {
			this.progressDialog.dismiss();
		}
	}
}
