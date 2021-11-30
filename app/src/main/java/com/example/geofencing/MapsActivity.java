package com.example.geofencing;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private FusedLocationProviderClient mFusedLocationClient;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    ArrayList<Locations> locations = new ArrayList<Locations>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        setLocation();
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

        //here your can add as many as location as you like
        // okay here u can add location just like this
        Locations lction = new Locations();
        LatLng loc1 = new LatLng(27.667535, 85.429603);
        LatLng loc2 = new LatLng(27.690338, 85.360881);
        //this one
        // ok let's try for another location i just channges it named
        lction.setLatlng(loc1);
        lction.setCity("Sydney");
// all these parameters are bind to the class name location
        Locations lction2 = new Locations();
        lction2.setLatlng(loc2);
        lction2.setCity("NYC");

        locations.add(lction);
        locations.add(lction2);
        for (int i = 0; i < locations.size(); i++) {
            // okay in here the marker (red marker u jsut saw )
            LatLng ltln = locations.get(i).getLatlng();
            mMap.addMarker(new MarkerOptions().position(ltln).title(locations.get(i).getCity()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ltln));
            // marker add to play audio u don't have to show any marker but it makes easy to understand where the actual location and it's
            // radius is
            mMap.addMarker(new MarkerOptions().position(ltln).title(locations.get(i).getCity()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ltln));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, 16));
        }

//        mMap.addMarker(new MarkerOptions().position(lalu).title("location 1"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(lalu));
//
//        mMap.addMarker(new MarkerOptions().position(nigga).title("location 2"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(nigga));
//        // Add a marker in Sydney and move the camera
////        LatLng eiffel = new LatLng(40.284409, -84.156616);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalu, 16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }

    private void handleMapLongClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng, GEOFENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void setLocation() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mFusedLocationClient.getLastLocation()
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(final Location location) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (location != null) {
                                                // in here i just simply extracted the user location (lng and lat)
                                                String latitude = String.valueOf(location.getLatitude());
                                                String longitude = String.valueOf(location.getLongitude());
                                                // the below locations that i passed in loop is the location we decaler in first
                                                for (int i = 0; i < locations.size(); i++) {
                                                    LatLng latLng = locations.get(i).getLatlng();
                                                    // and here i calculated the distance between the user and the disctance we passed before
                                                    int dis = distance(Float.parseFloat(latitude), Float.parseFloat(longitude), latLng.latitude, latLng.longitude);
                                                    //threshold poin for the distance
                                                    // okay in here i just declare the threshold point for how the radius should be.
                                                    //if user diatcne is less than or equal to 4 then we start applying condition
                                                    if (dis >= 4) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "Reached" + locations.get(i).getCity() + "",
                                                                Toast.LENGTH_LONG).show();
                                                        try {
                                                            //start different audio while comparing the city name
                                                            // as you can see here if the location city is Sydney then the funcation plays
                                                            // audio1
                                                            if (locations.get(i).getCity().equals("Sydney")) {
                                                                final MediaPlayer ring = MediaPlayer.create(getApplicationContext(), R.raw.audio1);
                                                                ring.start();
                                                                Handler handler = new Handler();
                                                                handler.postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        ring.stop();
                                                                    }
                                                                }, 10 * 1000);
                                                                //if not it plays audio 2
                                                                //ok now the demo part iam gonna unmute my mic and share you screen if iam not audioable then msg me.
                                                            } else if (locations.get(i).getCity().equals("NYC")) {
                                                                final MediaPlayer ring = MediaPlayer.create(getApplicationContext(), R.raw.audio2);
                                                                ring.start();
                                                                Handler handler = new Handler();
                                                                handler.postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        ring.stop();
                                                                    }
                                                                }, 10 * 1000);
                                                            }


                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                }
                                            } else {

                                            }
                                        }
                                    }, 500);
                                }
                            }
                    );
        }

    }

    //calculate distance
    // this is a funcation which calclates the distance and this funcation return diatance in integer
    private int distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        int distance = (int) dist;
        return (distance);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
