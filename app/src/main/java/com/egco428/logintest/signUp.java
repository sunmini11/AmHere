package com.egco428.logintest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class signUp extends AppCompatActivity {

    private LoginDataSource dataSource;
    EditText username;
    EditText password;
    Button summit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Actionbar
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f40d0d")));
        mActionBar.setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set back button

        username = (EditText) findViewById(R.id.usernameSignUp);
        password = (EditText) findViewById(R.id.passwordSignUp);
        summit = (Button) findViewById(R.id.summitBtn);

        dataSource = new LoginDataSource(this);
        dataSource.open();
    }

    //push submit button to signup
    public void pressSummit(View view) {

        String Username = username.getText().toString();
        String Password = password.getText().toString();

        String loginMessage = dataSource.findUsername(Username);

        //if username or password is blank
        if (Username.equals("") || Password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show(); //show message
        } else {
            //if username is already taken
            if (loginMessage.equals(Username)) {
                LoginMessage comment = dataSource.createLogin(Username, Password);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Username is already taken",
                        Toast.LENGTH_SHORT).show(); //show message
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
