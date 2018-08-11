package com.thebaileybrew.videogameinventory;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CatalogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CatalogActivity.class.getSimpleName();

    Boolean isFabMenuOpen = false;
    FloatingActionButton fabMenu;
    FloatingActionButton fabAddInventory; LinearLayout fabAddLayout; TextView fabAddText;
    FloatingActionButton fabDeleteInventory; LinearLayout fabDeleteLayout; TextView fabDeleteText;
    FloatingActionButton fabViewStats; LinearLayout fabViewLayout; TextView fabViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        initFabs();


        //TODO: Create RecyclerView, Adapter, Listeners
    }

    private void initFabs() {
        //Intitialize add FABS
        fabAddInventory = findViewById(R.id.inventory_add);
        fabAddLayout = findViewById(R.id.add_layout);
        fabAddText = findViewById(R.id.inventory_add_text);
        //Initialize delete FABS
        fabDeleteInventory = findViewById(R.id.delete_all);
        fabDeleteLayout = findViewById(R.id.delete_all_layout);
        fabDeleteText = findViewById(R.id.delete_all_text);
        //Initialize stats FABS
        fabViewStats = findViewById(R.id.stats_fab);
        fabViewLayout = findViewById(R.id.stats_layout);
        fabViewText = findViewById(R.id.inventory_stats_text);
        //Initialize  menu FAB
        fabMenu = findViewById(R.id.fab_menu);

        //Declare onClicks
        fabAddInventory.setOnClickListener(this);
        fabDeleteInventory.setOnClickListener(this);
        fabViewStats.setOnClickListener(this);
        fabMenu.setOnClickListener(this);
    }

    //Show and Hide the Secret FAB Menu
    private void showFABMenu() {
        isFabMenuOpen = true;
        fabAddLayout.setVisibility(View.VISIBLE);
        fabViewLayout.setVisibility(View.VISIBLE);
        fabDeleteLayout.setVisibility(View.VISIBLE);

        fabMenu.animate().rotationBy(90);
        fabAddLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
        fabDeleteLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_75))
                .translationX(-getResources().getDimension(R.dimen.standard_75));
        fabViewLayout.animate().translationX(-getResources().getDimension(R.dimen.standard_150)).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabAddText.setVisibility(View.INVISIBLE);
                fabDeleteText.setVisibility(View.INVISIBLE);
                fabViewText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fabAddText.setVisibility(View.VISIBLE);
                fabDeleteText.setVisibility(View.VISIBLE);
                fabViewText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
    private void closeFABMenu() {
        isFabMenuOpen = false;

        fabMenu.animate().rotationBy(-270);
        fabAddLayout.animate().translationY(0);
        fabDeleteLayout.animate().translationY(0).translationX(0);
        fabViewLayout.animate().translationX(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabAddText.setVisibility(View.INVISIBLE);
                fabDeleteText.setVisibility(View.INVISIBLE);
                fabViewText.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isFabMenuOpen) {
                    fabAddLayout.setVisibility(View.GONE);
                    fabDeleteLayout.setVisibility(View.GONE);
                    fabViewLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });



    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_menu:
                if(!isFabMenuOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
            case R.id.inventory_add:
                break;
            case R.id.delete_all:
                break;
            case R.id.stats_fab:
                break;
        }
    }
}
