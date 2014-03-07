package com.archit.beepthere;

import com.archit.beepthere.R;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	String address;
	EditText editText, editText2;
	Button setAlert;
	double latitude = 0.0;
	double longitude = 0.0;
	TextView currentDestination;
	Location location;
	Intent serviceIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		setAlert = (Button) findViewById(R.id.setAlert);
		location = new Location("");
		editText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_NEXT){
		        	if (v.getText().toString()!="")
		        	latitude = Double.parseDouble(v.getText().toString());
		        	editText2.requestFocus();
		        } 
		        return handled;
		    }
		});
		editText2.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (editText.getText().toString()!="") latitude = Double.parseDouble(editText.getText().toString());
					if (v.getText().toString()!="") longitude = Double.parseDouble(v.getText().toString());
					getLocationAndSetAlert(latitude, longitude);
					handled = true;
		        }
				return handled;
			}
		});
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setAlert(View v){
		if (editText.getText().toString().isEmpty() || editText2.getText().toString().isEmpty()){
			
		}
		else
		{
			latitude = Double.parseDouble(editText.getText().toString());
			longitude = Double.parseDouble(editText2.getText().toString());
			getLocationAndSetAlert(latitude, longitude);
		}
	}
	
	private void getLocationAndSetAlert(Double lat, Double longi)
    {
		
		if (lat > 90.0 || lat < -90.0){ 
			Toast.makeText(getBaseContext(), "Latitude values can be between -90 and 90 only", Toast.LENGTH_SHORT).show();
		}
		else if (longi > 180.0 || longi < -180.0){
			Toast.makeText(getBaseContext(), "Longitude values can be between -180 and 180 only", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, lat + ", " + longi, Toast.LENGTH_LONG).show();
			currentDestination = (TextView) findViewById(R.id.textView1);
			currentDestination.setText("Your current destination is set to ("+latitude+", "+longitude+")");
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			serviceIntent = new Intent(this,LocService.class);
			serviceIntent.putExtra("latitude", latitude);
			serviceIntent.putExtra("longitude", longitude);
			startService(serviceIntent);
			Log.d("Beeeeep","Service Started ");
		}
    }
	
}
