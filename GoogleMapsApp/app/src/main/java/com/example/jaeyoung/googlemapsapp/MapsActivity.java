package com.example.jaeyoung.googlemapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15; //15 seconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5.0f; //5 meters
    private Location myLocation;
    private static final float MY_LOC_ZOOM_FACTOR = 17.0f;
    private boolean isTracked = false;


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
            if (isGPSenabled) {
                Log.d("myMaps", "getLocation: GPS is enabled");
            }
            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Log.d("myMaps", "getLocation: Network is enabled");
            }

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("myMaps", "getLocation: No Provider is Enabled!");
            } else {
                canGetLocation = true;
                if (isGPSenabled == true) {
                    Log.d("myMaps", "getLocation: GPS Enabled - requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    Log.d("myMaps", "getLocation: Network GPS update request success");
                    */
                        return;
                    }
                }
                Log.d("MyMaps", "Permissions granted");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
                Log.d("MyMaps", "getLocation: GPS update request is happening");
                Toast.makeText(this, "Currently Using GPS", Toast.LENGTH_SHORT).show();

                if (isNetworkEnabled) {
                    Log.d("myMaps", "getLocation: Network Enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    /* locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    Log.d("myMaps", "getLocation: Network update request success");
                    */
                    Toast.makeText(this, "Currently Using Network", Toast.LENGTH_SHORT);
                }
            }
        } catch (Exception e) {
            Log.d("MyMaps", "Caught an exception in getLocation method");
            e.printStackTrace();
        }
    }


    public void trackMe(View view) {
        isTracked = true;
        if (isTracked == true) {
            getLocation();
            isTracked = false;
        }
        if (isTracked == false) {
            return;
        }
    }

    //the POI stuff
    public void searchPlaces(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.searchField);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Search Results"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
        LocationListener locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("MyMaps", "Location has changed");
                Toast.makeText(MapsActivity.this, "Location has changed", Toast.LENGTH_SHORT).show();
                dropAmarker(LocationManager.GPS_PROVIDER); //Drops gps markers
                locationManager.removeUpdates(locationListenerNetwork); //disables network updates when ur using GPS

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //setup a switch statement on status
                //case: LocationProvider.AVAILABLE --> output a message to Log.d and/or Toast
                //case: LocationProvider.OUT_OF_SERVICE --> request updates from NETWORK_PROVIDER
                //case: LocationProvider.TEMPORARILY_UNAVAILABLE --> request updates from NETWORK_PROVIDER
                switch (status) {
                    case LocationProvider.AVAILABLE:

                        Log.d("MyMaps", "LocationProvider is available");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:

                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);

                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                        break;
                    default:
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                        break;

                }
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        LocationListener locationListenerNetwork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //output a message in log.D and toast
                Log.d("MyMaps", "Network has changed");
                Toast.makeText(MapsActivity.this, "Network has changed", Toast.LENGTH_SHORT).show();

                //drop a marker on the map (create a method called drop a marker)
                dropAmarker(LocationManager.NETWORK_PROVIDER);

                //relaunch request for network location updates

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //output a message in Log.d and/or Toast
                Log.d("MyMaps", "Network onStatusChanged called");
                Toast.makeText(MapsActivity.this, "Network onStatusChanged method called", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

    public void dropAmarker(String provider) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
        }
        myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation == null) {
            //display a message in Log.d and/or Toast
        } else {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //display message in toast and log.d
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);

            //Add a shape for your marker that is not the default one.

            //if provider is gps it is a different color then if it is network provider
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation).
                            radius(1).
                            strokeColor(Color.RED).
                            strokeWidth(2).
                            fillColor(Color.RED));
            mMap.animateCamera(update);
        }


    }
}

