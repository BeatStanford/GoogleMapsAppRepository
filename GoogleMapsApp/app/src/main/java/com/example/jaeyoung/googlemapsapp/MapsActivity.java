package com.example.jaeyoung.googlemapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.View;
import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000*15; //15 seconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5.0f; //5 meters


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // changes initial location to place of birth: Seoul, Korea
        LatLng seoul = new LatLng(37.5665, 126.9780);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Born here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));


        /*drops a marker at current location
        LatLng f101 = new LatLng(32.958310, -117.190596);
        mMap.addMarker(new MarkerOptions().position(f101).title("Current Location: F101, CCA"));
        */
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("GoogleMapsApp", "Failed Permission check 1");
            Log.d("GoogleMapsApp", Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("GoogleMapsApp", "Failed Permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        mMap.setMyLocationEnabled(true);
    }

    public void changeMapView(View p) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get GPS Status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSenabled) Log.d("myMaps", "getLocation: GPS is enabled");

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled) Log.d("myMaps", "getLocation: Network is enabled");

            if(!isGPSenabled && !isNetworkEnabled) {
            Log.d("myMaps" , "getLocation: No Provider is Enabled!");
            } else {
                canGetLocation = true;
                if(isGPSenabled) {
                    Log.d("myMaps", "getLocation: GPS Enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                Log.d("myMaps", "getLocation: Network GPS update request success");
                Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
                if(isNetworkEnabled) {
                    Log.d("myMaps", "getLocation: Network Enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    Log.d("myMaps", "getLocation: Network update request success");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }



        } catch (Exception e){
                Log.d("MyMaps", "Caught an exception in getLocation method");
                e.printStackTrace();
            }
        }


    }



    }

