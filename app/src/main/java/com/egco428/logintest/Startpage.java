package com.egco428.logintest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Startpage extends AppCompatActivity {
    private MultitouchView m;
    public static boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f40d0d")));
        mActionBar.setTitle("AmHere");

        startMainPage();
    }

    public void startMainPage() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(m.pointerSize >= 2){
                    if (check==false){
                        handler.removeCallbacksAndMessages(null);
                    }
                    else {
                        Intent intent = new Intent(Startpage.this, MainActivity.class);
                        startActivity(intent);
                        check = false;
                    }
                }// this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, 1000);
            }
        }, 1000);


    }
}
