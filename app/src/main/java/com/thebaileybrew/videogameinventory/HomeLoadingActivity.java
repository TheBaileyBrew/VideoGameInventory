package com.thebaileybrew.videogameinventory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeLoadingActivity extends AppCompatActivity {
    TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_loading);
        welcomeMessage = findViewById(R.id.loading_progress_text);
        startLoader();


        //TODO: Set up Intent to load Catalog Activity
    }

    private void startLoader() {
        final Handler mHandler = new Handler();
        Runnable delayRunnable = new Runnable() {
            @Override
            public void run() {
                welcomeMessage.setText("DATABASE LOADED");
                openGamingDatabase();
            }
        };
        mHandler.postDelayed(delayRunnable, 20000);
    }

    private void openGamingDatabase() {
        //Intent call to main catalog
        Intent openGamingDatabase = new Intent(this, CatalogActivity.class);
        this.startActivity(openGamingDatabase);
    }
}
