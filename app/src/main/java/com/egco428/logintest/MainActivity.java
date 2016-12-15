package com.egco428.logintest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ProfileTracker profileTracker;

    private LoginDataSource dataSource;
    EditText username;
    EditText password;
    Button signIn;
    Button signUp;
    public String un;
    public static final String User = "username";
    public static final int DETAIL_REQ_CODE = 1001;

    //For Firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateUI();
                Intent intent = new Intent(MainActivity.this,FacebookMapsActivity.class);
                intent.putExtra(User,un);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                updateUI();
            }

            @Override
            public void onError(FacebookException error) {
                updateUI();
            }
        });

        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.usernameMain);
        password = (EditText)findViewById(R.id.passwordMain);

        signIn = (Button)findViewById(R.id.signin);
        signUp = (Button)findViewById(R.id.signup);

        dataSource = new LoginDataSource(this);
        dataSource.open();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
            }
        };

    }

    private void updateUI(){
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();


        if(loggedIn && (profile!=null)){
            username.setText(profile.getName());
            un = profile.getName();

        }else {
            username.setText(null);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        profileTracker.stopTracking(); //stop track profile
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void clickSignup(View view){

        startActivity(new Intent(MainActivity.this, signUp.class));
    }

    public void clickSignin(View view){
        checkPassword();
    }


    private void checkPassword(){

        String name = username.getText().toString();
        String loginMessage = dataSource.findpass(name);

        String passw = password.getText().toString();

        if (loginMessage.equals("")) {

            Toast.makeText(getApplicationContext(), "Login fail",
                    Toast.LENGTH_SHORT).show();

        }

        else {
            if (loginMessage.equals(passw)) {
                Toast.makeText(getApplicationContext(), "Login success",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this,SMSMapsActivity.class);
                intent.putExtra(User,name);

                startActivityForResult(intent,DETAIL_REQ_CODE);
                //startActivity(new Intent(MainActivity.this, AddFriend.class));
            }
            else {
                Toast.makeText(getApplicationContext(), "Login fail",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
