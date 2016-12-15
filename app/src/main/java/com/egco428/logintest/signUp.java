package com.egco428.logintest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class signUp extends AppCompatActivity {

    private LoginDataSource dataSource;
    EditText username;
    EditText password;
    Button summit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText)findViewById(R.id.usernameSignUp);
        password = (EditText)findViewById(R.id.passwordSignUp);
        summit = (Button)findViewById(R.id.summitBtn);

        dataSource = new LoginDataSource(this);
        dataSource.open();
    }

    public void pressSummit(View view){

        String Username = username.getText().toString();
        String Password = password.getText().toString();

        String loginMessage = dataSource.findUsername(Username);

        if(Username.equals("")||Password.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            if (loginMessage.equals(Username)) {
                LoginMessage comment = dataSource.createLogin(Username, Password);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Username is already taken",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
