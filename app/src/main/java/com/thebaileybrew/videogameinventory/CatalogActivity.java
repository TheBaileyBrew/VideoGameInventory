package com.thebaileybrew.videogameinventory;

import android.animation.Animator;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thebaileybrew.videogameinventory.customobjectsclasses.RecyclerItemTouchHelpListener;
import com.thebaileybrew.videogameinventory.database.InventoryContract;
import com.thebaileybrew.videogameinventory.database.InventoryCursorAdapter;
import com.thebaileybrew.videogameinventory.interfacefiles.CustomOnClickInterface;
import com.thebaileybrew.videogameinventory.interfacefiles.RecyclerItemTouchListener;
import com.thebaileybrew.videogameinventory.onclickprotocols.onClickInterface;

import java.util.Random;

public class CatalogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CatalogActivity.class.getSimpleName();

    private Boolean isFabMenuOpen = false;
    private FloatingActionButton fabMenu;
    private FloatingActionButton fabAddInventory; private LinearLayout fabAddLayout; private TextView fabAddText;
    private FloatingActionButton fabDeleteInventory; private LinearLayout fabDeleteLayout; private TextView fabDeleteText;
    private FloatingActionButton fabViewStats; private LinearLayout fabViewLayout; private TextView fabViewText;
    private boolean isFabViewed = false;
    private Animation animationFadeOut, animationFadeIn;

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout fadingLayoutForFabs;
    private RecyclerView recyclerView;
    private InventoryCursorAdapter inventoryCursorAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchCallBack;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        initFabs();
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseDetails();
    }

    private void displayDatabaseDetails() {
        final String[] projection = {
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

        inventoryCursorAdapter = new InventoryCursorAdapter(this, cursor, new CustomOnClickInterface() {

            //Defines what happens on item tap
            @Override
            public void onItemClick(View v, int position) {
                //TODO: Set up to call for updating Content Provider
            }

            //Defines what happens on long click
            @Override
            public void onLongClick(View v, int position) {
                Toast.makeText(CatalogActivity.this, "Current Row: " + position, Toast.LENGTH_SHORT).show();

                showOnLongClickDialog(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(inventoryCursorAdapter);
        //Set the swipe functionality for LEFT and RIGHT
        ItemTouchHelper.SimpleCallback leftTouchCallBack = new RecyclerItemTouchHelpListener(0, ItemTouchHelper.LEFT, new RecyclerItemTouchListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                //TODO: UPDATE QTY - 1
                Toast.makeText(CatalogActivity.this, "Swiped Left - Minus ONE", Toast.LENGTH_SHORT).show();
                inventoryCursorAdapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper.SimpleCallback rightTouchCallBack = new RecyclerItemTouchHelpListener(0, ItemTouchHelper.RIGHT, new RecyclerItemTouchListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                //TODO: UPDATE QTY + 1
                Toast.makeText(CatalogActivity.this, "Swiped Left - Plus ONE", Toast.LENGTH_SHORT).show();
                inventoryCursorAdapter.notifyDataSetChanged();
            }
        });
        new ItemTouchHelper(leftTouchCallBack).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(rightTouchCallBack).attachToRecyclerView(recyclerView);
        //Run the animation loader
        runLayoutAnimation(recyclerView);
    }


    private void runLayoutAnimation(RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_from_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /*
    * Initialize all Floating Action Buttons & Menu Layouts
    * Set onClickListeners to all FloatingAction Buttons
    */
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
        //Initialize the fade view
        fadingLayoutForFabs = findViewById(R.id.fade_layout);

        //Declare onClicks
        fabAddInventory.setOnClickListener(this);
        fabDeleteInventory.setOnClickListener(this);
        fabViewStats.setOnClickListener(this);
        fabMenu.setOnClickListener(this);
    }

    /*
    * Show and Hide the Secret FAB Menu
    */
    private void showFABMenu() {
        isFabMenuOpen = true;
        //On Menu open - make layouts visible
        fabAddLayout.setVisibility(View.VISIBLE);
        fabViewLayout.setVisibility(View.VISIBLE);
        fabDeleteLayout.setVisibility(View.VISIBLE);
        //Animate FABS - spin main, and translate x/y for other layouts
        fabMenu.animate().rotationBy(90);
        fabAddLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_140));
        fabDeleteLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_70))
                .translationX(-getResources().getDimension(R.dimen.standard_35));
        fabViewLayout.animate().translationX(-getResources().getDimension(R.dimen.standard_70)).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabAddText.setVisibility(View.INVISIBLE);
                fabDeleteText.setVisibility(View.INVISIBLE);
                fabViewText.setVisibility(View.INVISIBLE);
            }
            //Set the END animation for FAB layouts
            @Override
            public void onAnimationEnd(Animator animation) {
                fadingLayoutForFabs.setVisibility(View.VISIBLE);
                if (!isFabViewed) {
                    //Fade text in (anim) on first run
                    fabAddText.setAnimation(animationFadeIn);
                    fabViewText.setAnimation(animationFadeIn);
                    fabDeleteText.setAnimation(animationFadeIn);
                    fabAddText.setVisibility(View.VISIBLE);
                    fabDeleteText.setVisibility(View.VISIBLE);
                    fabViewText.setVisibility(View.VISIBLE);
                    //Fade text out (anim) on first run
                    fabAddText.setAnimation(animationFadeOut);
                    fabViewText.setAnimation(animationFadeOut);
                    fabDeleteText.setAnimation(animationFadeOut);
                    fabAddText.setVisibility(View.INVISIBLE);
                    fabDeleteText.setVisibility(View.INVISIBLE);
                    fabViewText.setVisibility(View.INVISIBLE);
                } else {
                    isFabViewed = true;
                    fabAddText.setVisibility(View.INVISIBLE);
                    fabDeleteText.setVisibility(View.INVISIBLE);
                    fabViewText.setVisibility(View.INVISIBLE);
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
    private void closeFABMenu() {
        isFabMenuOpen = false;
        //Animate FABS - spin all, and translate x/y for other layouts
        fabMenu.animate().rotationBy(-270);
        fabAddLayout.animate().rotationBy(-270);
        fabDeleteLayout.animate().rotationBy(-270);
        fabViewLayout.animate().rotationBy(-270);
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
                    fadingLayoutForFabs.setVisibility(View.GONE);
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

    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //Menu option selection
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
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Delete All Inventory method call
    */
    private void deleteAllInventory() {
        getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI,null,null);
        recyclerView.getAdapter().notifyDataSetChanged();
        displayDatabaseDetails();
    }

    /*
    * Insert dummy data based on random number generation, and String[] collection
    */
    private void insertDummyGame() {
        String[] gameNames = {"Call of Duty: Black Ops 4", "Super Mario Galaxy", "Spider-Man",
                "Monster Hunter Worlds", "Final Fantasy XIV", "Guitar Hero", "Red Dead Redemption II",
                "Super Smash Bros.", "Madden 18", "The Legend of Zelda: Breath of the Wild",
                "Fifa 2018", "Uncharted 4: A Thief's End"};
        String[] gamePrices = {"$14.44", "$31.16", "$191.23", "$20.14", "$48.32", "$10.09",
                "$79.01", "$321.19", "$1.14", "$3.99", "$42.55", "$500.00"};
        int[] gameSystems = {InventoryContract.InventoryEntry.SYSTEM_N3DS, InventoryContract.InventoryEntry.SYSTEM_NSWITCH,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_XBOXONE,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_PS3,
                InventoryContract.InventoryEntry.SYSTEM_N3DS, InventoryContract.InventoryEntry.SYSTEM_NSWITCH,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_XBOXONE,
                InventoryContract.InventoryEntry.SYSTEM_PS4, InventoryContract.InventoryEntry.SYSTEM_PS3};
        Random r2 = new Random(); Random r3 = new Random(); Random r4 = new Random();
        Random r5 = new Random(); Random r6 = new Random();
        int selection2 = r2.nextInt(12 - 1) + 1;
        int selection3 = r3.nextInt(12 - 1) + 1;
        int selection4 = r4.nextInt(12 - 1) + 1;
        int selection5 = r5.nextInt(12 - 1) + 1;
        int selection6 = r6.nextInt(12 - 1) + 1;
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
        values.put(InventoryContract.InventoryEntry.GAME_NAME, gameNames[selection2]);
        values.put(InventoryContract.InventoryEntry.GAME_SALE_PRICE, gamePrices[selection3]);
        values.put(InventoryContract.InventoryEntry.GAME_SYSTEM, gameSystems[selection5]);
        values.put(InventoryContract.InventoryEntry.GAME_QUANTITY, selection4);
        values.put(InventoryContract.InventoryEntry.GAME_SUGGESTED_PRICE,gamePrices[selection6]);
        values.put(InventoryContract.InventoryEntry.GAME_CONDITION, gameCondition);

        Uri blankUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
        recyclerView.getAdapter().notifyDataSetChanged();
        displayDatabaseDetails();
    }

    //Confirm deleting all database items
    private void showDeleteAllConfirmationDialog() {
        //Show an AlertDialog.Builder to set message and clickListeners
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Are you sure you want to delete all game inventory?");
        dialogBuilder.setNegativeButton(R.string.delete_them, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllInventory();
            }
        });
        dialogBuilder.setPositiveButton(R.string.save_them, new DialogInterface.OnClickListener() {
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

    //Prompt for what to do with a specific list item
    private void showOnLongClickDialog(int rowId) {

        final Uri currentGame = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, rowId);

        final AlertDialog.Builder longClickBuilder = new AlertDialog.Builder(this);
        longClickBuilder.setMessage("What would you like to do with this item?");
        longClickBuilder.setPositiveButton("Update It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Update
            }
        });
        longClickBuilder.setNegativeButton("Delete This", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(currentGame, null, null);
                //TODO: Delete
            }
        });
        longClickBuilder.setNeutralButton("See Details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Intent to view details
            }
        });
        AlertDialog longClick = longClickBuilder.create();
        longClick.show();
    }


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
                showDeleteAllConfirmationDialog();
                break;
            case R.id.stats_fab:
                if(isFabMenuOpen) { closeFABMenu(); }
                break;
        }
    }
}
