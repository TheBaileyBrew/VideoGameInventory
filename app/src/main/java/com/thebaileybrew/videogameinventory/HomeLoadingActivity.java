package com.thebaileybrew.videogameinventory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.View.VISIBLE;

public class HomeLoadingActivity extends AppCompatActivity {
    private TextView welcomeMessage;

    TextView hintOne, hintTwo, hintThree, hintFour;
    Animation in, in2, in3, in4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_loading);
        welcomeMessage = findViewById(R.id.loading_progress_text);
        hintOne = findViewById(R.id.hint_one);
        hintTwo = findViewById(R.id.hint_two);
        hintThree = findViewById(R.id.hint_three);
        hintFour = findViewById(R.id.hint_four);
        in = AnimationUtils.loadAnimation(this,R.anim.fadein);
        in2 = AnimationUtils.loadAnimation(this,R.anim.fadein);
        in3 = AnimationUtils.loadAnimation(this,R.anim.fadein);
        in4 = AnimationUtils.loadAnimation(this,R.anim.fadein);


        startLoader();


        //TODO: Set up Intent to load Catalog Activity
    }

    private void startLoader() {
        final Handler mHandler = new Handler(); final Handler mHandler2 = new Handler();
        final Handler mHandler3 = new Handler(); final Handler mHandler4 = new Handler();
        Runnable delayRunnable = new Runnable() {
            @Override
            public void run() {
                hintOne.setAnimation(in);
                hintOne.setVisibility(VISIBLE);
            }
        };
        mHandler.postDelayed(delayRunnable, 2500);
        Runnable delayRunnableTwo = new Runnable() {
            @Override
            public void run() {
                hintTwo.setAnimation(in2);
                hintTwo.setVisibility(VISIBLE);
            }
        };
        mHandler2.postDelayed(delayRunnableTwo, 5000);
        Runnable delayRunnableThree = new Runnable() {
            @Override
            public void run() {
                hintThree.setAnimation(in3);
                hintThree.setVisibility(VISIBLE);
            }
        };
        mHandler3.postDelayed(delayRunnableThree, 7500);
        Runnable delayRunnableFour = new Runnable() {
            @Override
            public void run() {
                hintFour.setAnimation(in4);
                hintFour.setVisibility(VISIBLE);
            }
        };
        mHandler4.postDelayed(delayRunnableFour, 10000);

        //Run final loading of app
        final Handler mHandlerLoad = new Handler();
        Runnable delayRunnableLoader = new Runnable() {
            @Override
            public void run() {
                welcomeMessage.setText("DATABASE LOADED");
                openGamingDatabase();
            }
        };
        mHandlerLoad.postDelayed(delayRunnableLoader, 12000);
    }

    private void openGamingDatabase() {
        final Handler mHandlerLoader = new Handler();
        Runnable delayRunnableLoaderer = new Runnable() {
            @Override
            public void run() {
                welcomeMessage.setText("DATABASE LOADED");
            }
        };
        mHandlerLoader.postDelayed(delayRunnableLoaderer, 14000);
                //Intent call to main catalog
        Intent openGamingDatabase = new Intent(this, CatalogActivity.class);
        this.startActivity(openGamingDatabase);
    }
}
