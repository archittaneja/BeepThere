package com.archit.beepthere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocService extends Service implements LocationListener {
	
	String locationProvider;
	
	Location mylocation; 
    Location destination = new Location("");
    float[] results;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; //1 minute
    protected LocationManager locationManager;
    NotificationManager notifier;
    Notification notification;
    PendingIntent pendingIntent;
    LocationManager locman;
    Context context;
	
	@Override
	public void onCreate(){
		mylocation = new Location("");
		locationProvider = "none";
		Log.d("MyLocationService","Created");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d("MyLocationService","Started");
		context = this;
		if (intent == null) {
			Log.d("NULL","INTENT");
		}
		this.destination.setLatitude(intent.getDoubleExtra("latitude", 0.0));
		this.destination.setLongitude(intent.getDoubleExtra("longitude", 0.0));
		notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mylocation = getUserLocation(context);
		return START_REDELIVER_INTENT;
	}

	private Location getUserLocation(Context context) {
		Location userLocation = new Location("");
		try{
		
		locman = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
		if (locman.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locationProvider = "GPS";
			Log.d("MyLocationService","GPS");
		}
		else if (locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locationProvider = "Network";
			Log.d("MyLocationService","Network");
		}
		
		if (locationProvider != "none"){
			if (locationProvider == "Network"){
				Log.d("location","requesting updates");
				locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				if (locman != null) {
                    userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (userLocation != null){
                    	
                    }
                }
				Log.d("MyLocationService","Updating Location Network");
			}
			if (locationProvider == "GPS"){
				locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				if (locman != null) {
                    userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
				Log.d("MyLocationService","Updating Location GPS");
			}
		}
		else {
			Log.d("Location Error","No location provider");
			Toast.makeText(context, "Turn on location services", Toast.LENGTH_SHORT).show();
			this.stopSelf();
		}
		}
		catch (Exception e){
			
		}
		if (userLocation == null){
			Toast.makeText(context, "Location Not available", Toast.LENGTH_SHORT).show();
			locman = null;
			this.stopSelf();
			return null;
		}
		
		return userLocation;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onLocationChanged(Location location) {
		Log.d("My Location", location.getLatitude()+", "+location.getLongitude());
		Log.d("Target Location", destination.getLatitude()+", "+destination.getLongitude());
		try{
		Log.d("MyLocationService","Location Changed");
		float distance = location.distanceTo(destination);
		Toast.makeText(context, distance + "", Toast.LENGTH_SHORT).show();
		if (distance <= 1000){
			try{
				notifier.cancel(2307);
			}
			catch(Exception e){
				
			}
	
			Log.d("location","" + distance);
			notification = new Notification(android.R.drawable.ic_menu_mylocation,"Proximity Alert", System.currentTimeMillis());
			pendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), 0);
			notification.setLatestEventInfo(context, "Proxomity Alert", "Distance from target: "+distance + " meters", pendingIntent);
			notification.defaults|=Notification.DEFAULT_SOUND;
			notifier.notify(2307, notification);
		}
		else {
			Log.d("location",distance+"");
			
		}
		}
		catch (Exception e){
			
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d("MyLocationService","Destoyed Service");
		notifier.cancel(2307);
		if (pendingIntent != null)pendingIntent.cancel();
		locman = null;
		this.stopSelf();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}



//getUserLocation method inspired from http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
