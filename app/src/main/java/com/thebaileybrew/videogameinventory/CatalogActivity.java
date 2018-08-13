package com.thebaileybrew.videogameinventory;

import android.animation.Animator;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thebaileybrew.videogameinventory.database.InventoryContract;
import com.thebaileybrew.videogameinventory.database.InventoryCursorAdapter;
import com.thebaileybrew.videogameinventory.onclickprotocols.onClickInterface;
import com.thebaileybrew.videogameinventory.onclickprotocols.onTouchRecyclerItemHelper;
import com.thebaileybrew.videogameinventory.onclickprotocols.onTouchRecyclerItemListener;

import java.util.Random;

public class CatalogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CatalogActivity.class.getSimpleName();

    Boolean isFabMenuOpen = false;
    FloatingActionButton fabMenu;
    FloatingActionButton fabAddInventory; LinearLayout fabAddLayout; TextView fabAddText;
    FloatingActionButton fabDeleteInventory; LinearLayout fabDeleteLayout; TextView fabDeleteText;
    FloatingActionButton fabViewStats; LinearLayout fabViewLayout; TextView fabViewText;

    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;
    InventoryCursorAdapter inventoryCursorAdapter;
    Cursor cursor;
    Cursor savingCursor;

    int dummyData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        initFabs();
        coordinatorLayout = findViewById(R.id.coordinator_layout);

    }



    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseDetails();
    }

    private void displayDatabaseDetails() {

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.GAME_NAME,
                InventoryContract.InventoryEntry.GAME_SYSTEM,
                InventoryContract.InventoryEntry.GAME_QUANTITY,
                InventoryContract.InventoryEntry.GAME_SALE_PRICE,
                InventoryContract.InventoryEntry.GAME_SUGGESTED_PRICE,
                InventoryContract.InventoryEntry.GAME_CONDITION};

        cursor = getContentResolver().query(
                InventoryContract.InventoryEntry.CONTENT_URI, projection,
                null,null,null,null);

        recyclerView = findViewById(R.id.recyclerView);

        inventoryCursorAdapter = new InventoryCursorAdapter(this, cursor, new onClickInterface() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(CatalogActivity.this, "Item clicked for update "
                        + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View v, int position) {
                Snackbar mySnackBar = Snackbar.make(coordinatorLayout, "Do something..", Snackbar.LENGTH_LONG)
                        .setAction("See Item Details...", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO: Call for Item Detail Intent
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorAccentRed));
                mySnackBar.show();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(inventoryCursorAdapter);
        ItemTouchHelper.SimpleCallback itemTouchCallBack = new onTouchRecyclerItemHelper(0,
                ItemTouchHelper.LEFT, new onTouchRecyclerItemListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
                Snackbar verifyDeletion = Snackbar.make(coordinatorLayout, "Undo Deletion?", Snackbar.LENGTH_LONG)
                    .setAction("Save That Game!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSwipeDeleteConfirmSnackBar(position);
                        }
                    });
                verifyDeletion.show();

                Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, position);
                getContentResolver().delete(currentItemUri, null, null);
                inventoryCursorAdapter.notifyDataSetChanged();
                displayDatabaseDetails();

            }
        });

        new ItemTouchHelper(itemTouchCallBack).attachToRecyclerView(recyclerView);
        runLayoutAnimation(recyclerView);
    }

    private void onSwipeDeleteConfirmSnackBar(int position) {
        Uri currentItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, position);
        
        inventoryCursorAdapter.notifyDataSetChanged();
        displayDatabaseDetails();
    }

    private void runLayoutAnimation(RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_from_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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
    //TODO: Add a new layout that adds a washout fade over the RecyclerView
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



    //Menu specific settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy:
                insertDummyGame();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteAllInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllInventory() {
        getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI,null,null);
        recyclerView.getAdapter().notifyDataSetChanged();
        displayDatabaseDetails();
    }

    private void insertDummyGame() {
        String[] gameNames = {"Call of Duty: Black Ops 4", "Super Mario Galaxy", "Spider-Man",
                "Monster Hunter Worlds", "Final Fantasy XIV", "Guitar Hero"};
        Double[] gamePrices = {14.44, 31.16, 19.23, 20.14, 48.32, 10.09};
        int[] gameSystems = {InventoryContract.InventoryEntry.SYSTEM_N3DS, InventoryContract.InventoryEntry.SYSTEM_NSWITCH,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_XBOXONE,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_PS3};
        Random r2 = new Random();
        int selection2 = r2.nextInt(5 + 1) + 1;
        String gameCondition = "";
        switch (selection2) {
            case 0:
            case 1:
                gameCondition = String.valueOf(R.string.condition_good); break;
            case 2:
            case 3:
                gameCondition = String.valueOf(R.string.condition_great); break;
            case 4:
            case 5:
                gameCondition = String.valueOf(R.string.condition_poor); break;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.GAME_NAME, gameNames[dummyData]);
        values.put(InventoryContract.InventoryEntry.GAME_SALE_PRICE, gamePrices[dummyData]);
        values.put(InventoryContract.InventoryEntry.GAME_SYSTEM, gameSystems[dummyData]);
        values.put(InventoryContract.InventoryEntry.GAME_QUANTITY, selection2);
        values.put(InventoryContract.InventoryEntry.GAME_SUGGESTED_PRICE,gamePrices[dummyData]);
        values.put(InventoryContract.InventoryEntry.GAME_CONDITION, gameCondition);

        Uri blankUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
        recyclerView.getAdapter().notifyDataSetChanged();
        displayDatabaseDetails();
        if (dummyData == 5) {
            dummyData = 0;
        } else {
            dummyData = dummyData + 1;
        }
    }

    private void showDeleteAllConfirmationDialog() {
        //Show an AlertDialog.Builder to set message and clickListeners

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Are you sure you want to delete all game inventory?");
        dialogBuilder.setPositiveButton(R.string.delete_them, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllInventory();
            }
        });
        dialogBuilder.setNeutralButton(R.string.save_them, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
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
                Intent addDevice = new Intent(CatalogActivity.this, AddActivity.class);
                if(isFabMenuOpen) { closeFABMenu(); }
                startActivity(addDevice);
                break;
            case R.id.delete_all:
                if(isFabMenuOpen) { closeFABMenu(); }
                deleteAllInventory();
                break;
            case R.id.stats_fab:
                if(isFabMenuOpen) { closeFABMenu(); }
                break;
        }
    }
}
