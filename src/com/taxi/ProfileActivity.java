package com.taxi;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity{
	ProgressDialog pd;
	private boolean exists = false;
	private boolean checked = false;
	Integer UID;
	String uname;
	String nname;
	String phonenum;
	Boolean uploadedNewImage = false;
	
	ImageView bmImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);
		UID = ((Member) this.getApplication()).ID;
		uname = ((Member) this.getApplication()).email;
		nname = ((Member) this.getApplication()).nickname;
		phonenum = ((Member) this.getApplication()).getPhoneNumber();
		new DisplayImageFromURL((ImageView) findViewById(R.id.imageView1))
        .execute("https://s3-us-west-1.amazonaws.com/familylocatorprofile/" + UID + ".jpeg");
		
		TextView tv3 = (TextView) findViewById(R.id.textView3);
		tv3.setText(nname);
		
		TextView tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(uname);
		
		TextView tv4 = (TextView) findViewById(R.id.phonetextview);
		tv4.setText(phonenum);
		
		final Button button = (Button) findViewById(R.id.browse);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (!checked) {
                    Toast.makeText(getApplicationContext(), "Please wait a moment...",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!exists) {
                    Toast.makeText(getApplicationContext(), "You must first create the bucket",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                }            	
            }
        });
        
        final Context context = this;
        final Button updatennamebut = (Button) findViewById(R.id.updateNickname);
        updatennamebut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(context, NewNnamePopup.class));
            }
        });
        
        final Button updatepassbut = (Button) findViewById(R.id.updatePass);
        updatepassbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(context, NewPassPopup.class));
            }
        });
        
        final Button updatephonebut = (Button) findViewById(R.id.phoneButton);
        updatephonebut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(context, NewPhonePopup.class));
            }
        });
        
        
        new CheckBucketExists().execute();
	}
	
	 /*
     * When we get a Uri back from the gallery, upload the associated
     * image/video
     */
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) 
    {
        if (resCode == Activity.RESULT_OK && data != null) 
        {
            Uri uri = data.getData();
            if (uri != null) {
                TransferController.upload(this, uri);
                
                //----------- PLEASE DO NOT REMOVE THIS TOAST BELOW WHILE CLEANINF UP OTHER UNNEEDED TOASTS -----------------
                Toast msg = Toast.makeText(ProfileActivity.this, "Profile has been updated", Toast.LENGTH_LONG);
                msg.show();
                Intent returnToHome = new Intent (this, MainActivity.class);
                startActivity(returnToHome);
                finish();
            }
        }
        
    }
    
    
    /* makes sure that we are up to date on the transfers */

		// used to download a photo from AWS using a URL
		private class DisplayImageFromURL extends AsyncTask<String, Void, Bitmap> {
	        ImageView bmImage;
	
	        @Override
	        protected void onPreExecute() {
	            // TODO Auto-generated method stub
	            super.onPreExecute();
	            pd = new ProgressDialog(ProfileActivity.this);
	            pd.setMessage("Loading...");
	            pd.show();
	        }
	        public DisplayImageFromURL(ImageView bmImage) {
	            this.bmImage = bmImage;
	        }
	        protected Bitmap doInBackground(String... urls) {
	            String urldisplay = urls[0];
	            Bitmap mIcon11 = null;
	            try {
	                InputStream in = new java.net.URL(urldisplay).openStream();
	                mIcon11 = BitmapFactory.decodeStream(in);
	                mIcon11= Bitmap.createScaledBitmap(mIcon11, 150, 150, true);
	            } catch (Exception e) {
	                Log.e("Error", e.getMessage());
	                e.printStackTrace();
	            }
	            return mIcon11;
	        }
	        protected void onPostExecute(Bitmap result) {
	            bmImage.setImageBitmap(result);
	            pd.dismiss();
	        }
	}
		
		private class CheckBucketExists extends AsyncTask<Object, Void, Boolean> {

	        @Override
	        protected Boolean doInBackground(Object... params) {
	            Util.getS3Client(getApplicationContext());
	            return Util.doesBucketExist();
	        }

	        @Override
	        protected void onPostExecute(Boolean result) {
	            checked = true;
	            exists = result;
	        }
	    }

}