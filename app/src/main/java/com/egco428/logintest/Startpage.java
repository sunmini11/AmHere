package com.egco428.logintest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Startpage extends AppCompatActivity {
    private MultitouchView m;
    public static boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        scheduleSendLocation();
    }

    public void scheduleSendLocation() {
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
