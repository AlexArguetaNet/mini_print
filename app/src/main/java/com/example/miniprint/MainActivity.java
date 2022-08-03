package com.example.miniprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements HomeFragment.IHome {
    
    final String TAG = "demoos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Adding an instance of HomeFragment to MainActivity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainContainer, new HomeFragment())
                .commit();


    }



}