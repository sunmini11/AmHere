package com.egco428.logintest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SMSMapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    public String Username;
    public static final String User = "username";
    public static final int DETAIL_REQ_CODE = 1001;
//    public double Lati;
//    public double Long;

    public boolean onmap = false;
    double Lati = 13.809615;
    double Long = 100.310501;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String mprovider;
    private ContDataSource dataSource;
    protected List<ContMessage> values;
    private ArrayAdapter<ContMessage> loginArrayAdapter;

    private ArrayList<LoginMessage> datalist;
    //For Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    //For seeing friends
    private final int Delay_SECONDS = 1000;

    ArrayList<String> user = new ArrayList<>();
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> lon = new ArrayList<>();

    LocationManager mLocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsmaps);
        Username = getIntent().getStringExtra(MainActivity.User);

//        Lati = getIntent().getDoubleExtra(MainActivity.La,0);
//        Long = getIntent().getDoubleExtra(MainActivity.Lo,0);

//        dataSource = new ContDataSource(this);
//        dataSource.open();
//        //values = dataSource.getAllComments(User);
//        Cursor allNumber = dataSource.findnumber(User);

        //loginArrayAdapter = new loginArrayAdapter(this,0,values);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, true);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 1000, 1, this);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,this);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,0,0,this);
//            Lati = location.getLatitude();
//            Long = location.getLongitude();
            if (location != null && onmap){
                onLocationChanged(location);}
            else{
            }
                //Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
            scheduleSendLocation();
        }

        //Firebase
        datalist = new ArrayList();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Config.DATABASE_REF);

    }


    @Override
    public void onLocationChanged(Location location) {

        mMap.clear();

        Lati =  location.getLatitude();
        Long = location.getLongitude();

        LatLng sydney = new LatLng(Lati, Long);
        mMap.addMarker(new MarkerOptions().position(sydney).title("You are here"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20));

        //get data
        double getLat = Lati;
        double getLon = Long;
        DataFirebase dataFirebase = new DataFirebase(Username, getLat, getLon);
        myRef.child(Username).setValue(dataFirebase);

        onmap = true;
    }

    public void markFriend(){
        for(int i = 0;i<lat.size();i++)
        {
            String UN = user.get(i).toString();
            if(UN.equals(Username)){
            }
            else{
                double la = lat.get(i);
                double lo = lon.get(i);
                double dis = distance(Lati,Long,la,lo);
                if(dis<0.5){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(la,lo)).title(UN));
                }
            }
        }
    }

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

    public void scheduleSendLocation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserData();
                markFriend();// this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, Delay_SECONDS);
            }
        }, Delay_SECONDS);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void collectPhoneNumbers(Map<String,Object> users) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            user.add((String) singleUser.get("usernameFb"));
            lat.add((Double) singleUser.get("lat"));
            lon.add((Double)singleUser.get("lon"));
        }

        System.out.println("data123"+user.toString()+lat.toString()+lon.toString());
    }

    public void UserData(){

        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

//        });
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20));
        onmap = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            String UN = Username;
            Intent intent = new Intent(this,AddFriend.class);
            intent.putExtra(User,UN);
            startActivityForResult(intent,DETAIL_REQ_CODE);

            //startActivity(new Intent(SMSMapsActivity.this, AddFriend.class));
            return true;
        }
        if (id == android.R.id.home) {
            myRef.child(Username).removeValue();
            LoginManager.getInstance().logOut();
            finish();
        }

      return super.onOptionsItemSelected(item);
    }

    public void sendSMS(View view) {
        dataSource = new ContDataSource(this);
        dataSource.open();
        //values = dataSource.getAllComments(User);
        Cursor allNumber = dataSource.findnumber(Username);
        allNumber.moveToFirst();

        while (!allNumber.isAfterLast()) {
            String phoneNo = allNumber.getString(2);
            String msg = "I'm here" + " " + "Latituge: " + Lati + "  " + "Longitude: " + Long;
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if(myRef.child(Username)!=null){
            myRef.child(Username).removeValue();
        }
        super.onDestroy();
    }

}
