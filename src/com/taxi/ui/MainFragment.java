package com.taxi.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.taxi.MainActivity;
import com.taxi.Member;
import com.taxi.Place;
import com.taxi.ProfileActivity;
import com.taxi.R;
import com.taxi.custom.CustomActivity;

public class MainFragment extends Fragment implements OnClickListener
{

	private MapView mMapView;
	private GoogleMap mMap;
	private Context context;
	private Member currentMember;
	private Double mLat = 0.0;
	private Double mLng = 0.0;
	private boolean contactsDisplay;
	private boolean placesDisplay;
	
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main_container, null);
		context = v.getContext ();
		
		try {
			initMap(v, savedInstanceState);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace ();
		}
		currentMember = ((Member) getActivity().getApplication());
		
		try {
        	new GetDisplayDataTask ().execute ().get ();
			new GetPlacesDataTask (currentMember.getID()).execute ().get ();
			new CalculateLocationTask().execute().get();
        } catch (Exception e) {
        	Log.d("error", e.toString());
        }
		contactsDisplay = false;
		placesDisplay = true;
		PushService.setDefaultPushCallback(this.getActivity(), MainActivity.class);
		ParsePush.subscribeInBackground(currentMember.getNickname());
		
		initButtons (v);
		
		return v;
	}
	
	public void callAsynchronousTask() {
	    final Handler handler = new Handler();
	    Timer timer = new Timer();
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() { 
	                	Log.d("test >.<", "run!!");
	                    try {
	                    	new GetDisplayDataTask ().execute ().get ();
	            			new GetPlacesDataTask (currentMember.getID()).execute ().get ();
	            			new CalculateLocationTask().execute().get();
	                    } catch (Exception e) {
	                    	Log.d("error", e.toString());
	                    }
	                }
	            });
	        }
	    };
	    timer.schedule(doAsynchronousTask, 0, 10000); 
	}

	
	private void initButtons(View v) {
		View b = v.findViewById(R.id.btnCheckin);
		b.setOnTouchListener(CustomActivity.TOUCH);
		b.setOnClickListener(this);
		
		b = v.findViewById(R.id.btnLoc);
		b.setOnTouchListener(CustomActivity.TOUCH);
		b.setOnClickListener(this);
		
		b = v.findViewById(R.id.btnPlaces);
		b.setOnTouchListener(CustomActivity.TOUCH);
		b.setOnClickListener(this);
	}

	private void initMap(View v, Bundle savedInstanceState) throws GooglePlayServicesNotAvailableException {
		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
	}

	private void setupMapMarkers (Integer id) throws IOException {
		String nname = "404";
		Double latitude = 0.0;
		Double longitude = 0.0;
		Integer uid = id;
		HashMap<String, Object> map = currentMember.getDisplay (id);
		if (id.equals (currentMember.getID ())){
			latitude =  Double.valueOf((String) map.get ("lat"));
			longitude = Double.valueOf((String) map.get ("lng"));
			mLat = latitude;
			mLng = longitude;
			nname = currentMember.getNickname();
		} else if (currentMember.inDisplay (id)) {
			nname = (String) map.get ("nickname");
			latitude = Double.valueOf((String) map.get ("lat"));
			longitude = Double.valueOf((String) map.get ("lng"));
		}
		Geocoder gc = new Geocoder (context, Locale.getDefault ());
		List<Address> list = gc.getFromLocation (latitude, longitude, 1);
		Address add = list.get(0);
		String street = add.getAddressLine(0);
		String city = add.getAddressLine(1);
		String adress = street + "\n" + city;

		createMarker (uid, latitude, longitude, nname, adress);
	}

	private void createMarker (Integer uid, Double lat, Double lng, String nname, String address) {
		LatLng latlng = new LatLng (lat, lng);
		MarkerOptions options = new MarkerOptions()		
		.position (latlng)
		.title (nname)
		.snippet (address);

		if (uid.equals(currentMember.getID())){
			options.icon (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		}else {
			options.icon (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		}

		mMap.addMarker (options);
		mMap.setOnInfoWindowClickListener (new OnInfoWindowClickListener () {
			@Override
			public void onInfoWindowClick (Marker m) {
				if (m.getTitle().equals (currentMember.getNickname())){
					Intent intent = new Intent (getActivity (), ProfileActivity.class);
					startActivity (intent);
				}
			}
		});

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put ("marker", options);
		map.put ("uid", uid);
		map.put ("lat", String.valueOf(lat));
		map.put ("lng", String.valueOf(lng));
		map.put ("nickname", nname);

		currentMember.addDisplay (uid, map);

		mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latlng, 18));
	}

	private void loadMarkers() {
		for (Integer i: currentMember.display.keySet()) {
			try {
				setupMapMarkers (i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private final View mContents;
		private ImageView picture;

		@SuppressLint("InflateParams") 
		CustomInfoWindowAdapter () {
			this.mContents = getActivity().getLayoutInflater().inflate(R.layout.map_popup, null);
		}

		@Override
		public View getInfoWindow (Marker marker) {
			picture = (ImageView) mContents.findViewById (R.id.imageView1);
			TextView tv_nickname = (TextView) mContents.findViewById(R.id.title);
			TextView tv_address = (TextView) mContents.findViewById (R.id.snippet);

			String address = marker.getSnippet();
			SpannableString addressFormat = new SpannableString (address);
			String nickname = marker.getTitle();
			SpannableString nicknameFormat = new SpannableString (nickname);

			String UID = currentMember.IDfromNickname(nickname);
			try {
				new DisplayImageFromURL ()
				.execute ("https://s3-us-west-1.amazonaws.com/familylocatorprofile/" + UID + ".jpeg")
				.get ();
			} catch (Exception e) {
				e.printStackTrace ();
			} 

			if (nickname != null) {
				nicknameFormat.setSpan (new StyleSpan (Typeface.BOLD), 0, nickname.length (), 0);
				tv_nickname.setText (nicknameFormat);
			}
			else {
				tv_nickname.setText ("undefined user");
			}

			addressFormat.setSpan (new StyleSpan (Typeface.ITALIC), 0, addressFormat.length (), 0);
			tv_address.setText (addressFormat);

			return mContents;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}


		private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {
			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = new ProgressDialog(getActivity());
				pd.setMessage("Loading...");
				pd.show();
			}
			@Override
			protected Bitmap doInBackground(String... urls) {
				String urldisplay = urls[0];
				Bitmap mIcon11 = null;
				try { 
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
					mIcon11= Bitmap.createScaledBitmap(mIcon11, 100, 100, true);
					picture.setImageBitmap (mIcon11);
				} catch (Exception e) {
					Log.e("Error: Problem downloading image: ", e.getMessage());
					e.printStackTrace();
				}
				return mIcon11;
			}
			
			@Override
			protected void onPostExecute (Bitmap result) {
				pd.dismiss ();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		mMap = mMapView.getMap();
		callAsynchronousTask();
		if (mMap != null) {
			mMap.setInfoWindowAdapter (new CustomInfoWindowAdapter ());

			mMap.clear();
			currentMember.userNN = currentMember.getNickname();
			try {
				String user = currentMember.IDfromNickname(currentMember.userNN);
				setupMapMarkers (Integer.valueOf(user));
			}catch (IOException e) {}
			drawPlaces();
		}
	}

	@Override
	public void onPause () {
		mMapView.onPause ();
		if (mMap != null)
			mMap.setInfoWindowAdapter (null);
		super.onPause ();
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnPlaces) {
			if(placesDisplay){
				placesDisplay = false;
				mMap.clear();
				if(contactsDisplay){
					loadMarkers();
				}else{
					try {
						String user = currentMember.IDfromNickname(currentMember.userNN);
						setupMapMarkers (Integer.valueOf(user));
					} catch(Exception e){}
				}
			}else{
				placesDisplay = true;
				drawPlaces();
			}
			mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (new LatLng(mLat, mLng), 18));
		}
		
		else if (v.getId() == R.id.btnLoc) {
			mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (new LatLng(mLat, mLng), 20));
		}
		
		else if (v.getId() == R.id.btnCheckin) {
			if(contactsDisplay){
				contactsDisplay = false;
				mMap.clear();
				try {
					String user = currentMember.IDfromNickname(currentMember.userNN);
					setupMapMarkers (Integer.valueOf(user));
				} catch(Exception e){}
				if(placesDisplay){
					drawPlaces();
				}

			}else{
				contactsDisplay = true;
				loadMarkers();
			}
			mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (new LatLng(mLat, mLng), 18));
		}
		
	}

	class GetDisplayDataTask extends AsyncTask<String, String, String> {
		private ProgressDialog progressDialog = new ProgressDialog (context);
		
		@Override
		protected void onPreExecute () {
			progressDialog.setMessage ("Getting data");
			progressDialog.show ();
			progressDialog.setOnCancelListener (new OnCancelListener () {
				@Override
				public void onCancel (DialogInterface arg0) {
					GetDisplayDataTask.this.cancel (true);
				}
			});
		}       

		@Override
		protected String doInBackground (String... params) {
			ParseQuery<ParseObject> inner = ParseQuery.getQuery("circles");
            inner.whereEqualTo("adminID", currentMember.getID());
            inner.whereEqualTo("shareLocation", 0);
        	ParseQuery<ParseObject> outter = ParseQuery.getQuery("users");
        	outter.whereMatchesKeyInQuery("uid", "membersID", inner);
        	List<ParseObject> result = null;
        	try {
                result = outter.find();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        	
        	if (result.size() != 0){
        		currentMember.clearDisplay();
        		for (ParseObject o : result) {
        			HashMap<String, Object> map = new HashMap<String, Object>();
        			Integer id = o.getInt ("uid");
        			String nname = o.getString ("nickname");
        			String lat = o.getString ("latitude");
        			String lng = o.getString ("longitude");
        			map.put ("uid", id);
        			map.put ("lat", lat);
        			map.put ("lng", lng);
        			map.put ("nickname", nname);
        			currentMember.addDisplay (id, map);
        		}
        	} else {
        		ParseQuery<ParseObject> q = ParseQuery.getQuery("users");
                q.whereEqualTo("uid", currentMember.getID());
                List<ParseObject> result2 = null;
                try {
                    result2 = q.find();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                HashMap<String, Object> map = new HashMap<String, Object>();
    			Integer id = result2.get(0).getInt ("uid");
    			String nname = result2.get(0).getString ("nickname");
    			String lat = result2.get(0).getString ("latitude");
    			String lng = result2.get(0).getString ("longitude");
    			map.put ("uid", id);
    			map.put ("lat", lat);
    			map.put ("lng", lng);
    			map.put ("nickname", nname);
    			currentMember.addDisplay (id, map);
        	}
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			return null;
		}

		@Override
		protected void onPostExecute (String v) {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
		}
	}
	
	private void drawPlaces () {
		ArrayList<Place> places = currentMember.getPlaces();
		Log.d("PLACE", String.valueOf(places.size()));
		Geocoder gc = new Geocoder(context);
		if(places.size() > 0)
			currentMember.clearShape();
		for (Place p : places) {
			Double lat =  null;
			Double lng = null;
			try {
				List<Address> list = gc.getFromLocationName(p.getAddress() + p.getCity(), 1);
				Address add = list.get(0);
				lat = add.getLatitude();
				lng = add.getLongitude();
				p.setLat(lat);
				p.setLng(lng);
			} catch (Exception e) {
				
			}
			CircleOptions options = new CircleOptions()
				.center(new LatLng(lat, lng))
				.radius(50)
				.fillColor(0x330000FF)
				.strokeColor(Color.BLUE)
				.strokeWidth(3);
			currentMember.addShape(mMap.addCircle(options));
		}
		
	}
	
	private class CalculateLocationTask extends AsyncTask<String, String, String> {
		private ProgressDialog progressDialog = new ProgressDialog (context);

		@Override
		protected void onPreExecute () {
			progressDialog.setMessage ("Getting data");
			progressDialog.show ();
			progressDialog.setOnCancelListener (new OnCancelListener () {
				@Override
				public void onCancel (DialogInterface arg0) {
					CalculateLocationTask.this.cancel (true);
				}
			});
		}       

		@SuppressLint({ "UseSparseArrays", "SimpleDateFormat" }) @Override
		protected String doInBackground (String... params) {
			ArrayList<Place> places = currentMember.getPlaces();
			HashMap<Integer, HashMap<String, Object>> contacts = currentMember.getDisplay();
			for (Integer i : contacts.keySet()) {
				HashMap<String, Object> user = contacts.get(i);
				String nickname = (String) user.get("nickname");
				if(!(nickname.equals(currentMember.getNickname()))){
					Double latA = Double.valueOf((String) user.get("lat"));
					Double lngA = Double.valueOf((String) user.get("lng"));
					Location usrLoc = new Location(nickname);
					usrLoc.setLatitude(latA);
					usrLoc.setLongitude(lngA);
					for (Place place : places) {
						Double latB = place.getLat();
						Double lngB = place.getLng();
						Location placeLoc = new Location(place.getName());
						placeLoc.setLatitude(latB);
						placeLoc.setLongitude(lngB);
						double res = usrLoc.distanceTo(placeLoc);
						Integer usr = (Integer) user.get("uid");
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
						String strDate = sdf.format(c.getTime());
						String name = place.getName();
						if(!currentMember.containsNotification(usr, name)){
							if(res > 50){
								currentMember.setNotification(usr, name, false);
							}else{
								currentMember.setNotification(usr, name, true);
							}
						}else if(res > 50){
							if(currentMember.checkNotification(usr, name)){
								currentMember.toggleNotification(usr, name);
								ParsePush push = new ParsePush();
								push.setChannel(currentMember.getNickname());
								push.setMessage(nickname + " left " + place.getName() + " at " + strDate);
								push.sendInBackground();
							}
						}else {
							if(!currentMember.checkNotification(usr, name)){
								currentMember.toggleNotification(usr, name);
								ParsePush push = new ParsePush();
								push.setChannel(currentMember.getNickname());
								push.setMessage(nickname + " arrived at " + place.getName() + " at " + strDate);
								push.sendInBackground();
							}
						}
					}
				}
			}
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute (String arg0) {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
		}
	}

	
	class GetPlacesDataTask extends AsyncTask<String, String, String> {
		private ProgressDialog progressDialog = new ProgressDialog (context);
		private Integer UID = null;
		
		GetPlacesDataTask (Integer id){
			this.UID = id;
		}

		@Override
		protected void onPreExecute () {
			progressDialog.setMessage ("Getting data");
			progressDialog.show ();
			progressDialog.setOnCancelListener (new OnCancelListener () {
				@Override
				public void onCancel (DialogInterface arg0) {
					GetPlacesDataTask.this.cancel (true);
				}
			});
		}       

		@Override
		protected String doInBackground (String... params) {			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("places");
			query.whereEqualTo("uid", UID);
			List<ParseObject> placesList = null;
			try{
				placesList = query.find();
				currentMember.clearPlaces();
				for (int i = 0; i < placesList.size(); i++) {
					currentMember.addPlace(placesList.get(i).getString("name"), 
							placesList.get(i).getString("street"), 
							placesList.get(i).getString("city"), 
							placesList.get(i).getString("latitude"), 
							placesList.get(i).getString("longitude"));
				}
			}catch (Exception e){
			}

			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
			return null;
		}

		@Override
		protected void onPostExecute (String arg0) {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss ();
			}
		}
	}
}
