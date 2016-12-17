package com.egco428.logintest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.login.LoginManager;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FacebookMapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    protected GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;

    double Lati = 13.809615;
    double Long = 100.310501;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String mprovider;

    private boolean onmap = false;
    private Button shareLocation;
    private ArrayList<DataFirebase> datalist;

    //For Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    String getUsername;
    private final int Delay_SECONDS = 1000;

    ArrayList<String> user = new ArrayList<>();
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> lon = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Actionbar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f40d0d")));
        mActionBar.setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button

        //Receive username from MainActivity
        getUsername = getIntent().getStringExtra(MainActivity.User);


        //Firebase
        datalist = new ArrayList();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Config.DATABASE_REF);

        //GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mprovider = locationManager.getBestProvider(criteria, true);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, this);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 5000, 0, this);

            if (location != null && onmap) {
                onLocationChanged(location);
            } else {
            }

            scheduleSendLocation();
        }

        //Push button to post location on Facebook
        shareLocation = (Button) findViewById(R.id.postBtn);
        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePendingAction();
                performPublish(PendingAction.POST_LOCATION);
            }
        });


    }

    //Add latitude, longitude and username when location change
    @Override
    public void onLocationChanged(Location location) {

        mMap.clear();

        //get current latitude and longitude
        Lati = location.getLatitude();
        Long = location.getLongitude();

        //set user's location on Map
        LatLng sydney = new LatLng(Lati, Long);
        mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
         mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //get data
        double getLat = Lati;
        double getLon = Long;
        DataFirebase dataFirebase = new DataFirebase(getUsername, getLat, getLon);
        myRef.child(getUsername).setValue(dataFirebase);

        onmap = true;
    }

    /*
    Add the other users's location on map and marker
    Distance from user to them have to less than 500 meters
    */
    public void markFriend() {
        for (int i = 0; i < lat.size(); i++) {
            String UN = user.get(i).toString();
            if (UN.equals(getUsername)) {
            } else {
                double la = lat.get(i);
                double lo = lon.get(i);
                double dis = distance(Lati, Long, la, lo);
                if(dis<0.5){
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(la, lo))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title(UN)
                            .snippet(String.valueOf(la)+","+String.valueOf(lo)));
                }
            }
        }
    }

    //Read data from Firebase every 1 second
    public void scheduleSendLocation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserData();
                markFriend();
                handler.postDelayed(this, Delay_SECONDS);
            }
        }, Delay_SECONDS);
    }

    //Calculate distance between user and the others
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Log.d("Latitude","status");
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Lati, Long);
        mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));
        onmap = true;
    }

    private static final String PERMISSION = "publish_actions";

    //Kind of posting which waiting for processing
    private enum PendingAction {
        NONE,
        POST_LOCATION
    }

    //posting will process after get publish_action permission
    private PendingAction pendingAction = PendingAction.NONE;

    //Post method to check login status
    private void performPublish(PendingAction action) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action; //recognise the action
            handlePendingAction();
        }
    }

    //post location link on Facebook
    private void handlePendingAction() {
        PendingAction oldPendingAction = pendingAction;
        pendingAction = PendingAction.NONE;

        switch (oldPendingAction) {
            case NONE:
                break;
            case POST_LOCATION:
                PostLocation();
                break;
        }
    }

    //Pst location on Facebook
    public void PostLocation() {
        Profile profile = Profile.getCurrentProfile();

        //link's information
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("I'm here at Latitude:" + Lati + " Longitude:" + Long)
                .setContentDescription("My current Location")
                .setContentUrl(Uri.parse("https://www.google.co.th/maps/?q=" + Lati + "," + Long + "&zoom=3"))
                .setImageUrl(Uri.parse("http://images.telegiz.com/data/images/full/3508/google-maps.png"))
                .build();


        if (profile != null && hasPublishPermission()) {
            ShareApi.share(content, shareCallback);
        } else {
            pendingAction = PendingAction.POST_LOCATION;
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PERMISSION)); //request permission from user
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    //to be a callback of the post
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
        }

        //when post failed
        @Override
        public void onError(FacebookException error) {
            String title = "Post Failed";
            String msg = error.getMessage();
            showResult(title, msg);
        }

        //when post successful
        @Override
        public void onSuccess(Sharer.Result result) {
            if (result.getPostId() != null) {
                String title = "Facebook";
                String msg = String.format("Post Successful");
                showResult(title, msg);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(FacebookMapsActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton("OK", null)
                    .show();
        }
    };

    //when press back on your device
    public void onDestroy() {
        if (myRef.child(getUsername) != null) {
            myRef.child(getUsername).removeValue(); //remove data from Firebase
            LoginManager.getInstance().logOut(); //log out from facebook
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    //when push back button on interface
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            myRef.child(getUsername).removeValue();
            LoginManager.getInstance().logOut();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.removeUpdates(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Get latitude, longitude and user's name
    private void GetUserData(Map<String,Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            user.add((String) singleUser.get("usernameFb"));
            lat.add((Double) singleUser.get("lat"));
            lon.add((Double)singleUser.get("lon"));
        }
        System.out.println("userdata"+user.toString()+lat.toString()+lon.toString());
    }

    //Get user's data from Firebase
    public void UserData(){
            myRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Get map of users in datasnapshot
                    GetUserData((Map<String,Object>) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });
    }

}
