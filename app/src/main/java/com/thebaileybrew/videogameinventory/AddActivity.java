package com.thebaileybrew.videogameinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thebaileybrew.videogameinventory.customobjectsclasses.NumberTextWatcher;
import com.thebaileybrew.videogameinventory.database.InventoryContract;
import com.thebaileybrew.videogameinventory.upcquery.upc;
import com.thebaileybrew.videogameinventory.upcquery.upcLoader;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<upc>> {
    private static final String TAG = AddActivity.class.getSimpleName();
    private static final int SCAN_LOADER_ID = 1;
    private final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
    public String requestUrl = "https://api.upcitemdb.com/prod/trial/lookup";

    private TextInputEditText mGameNameEditText;
    public TextInputLayout mGameNameLayout;

    private TextInputEditText mGamePriceEditText;
    private TextInputLayout mGamePriceLayout;
    private String mSuggestedPrice;
    private final String pricingPattern = "\\$\\d{1,6}\\.\\d{2}";
    public String finalDollarFormat = "$\\d{2}\\.\\d{2}";

    private Spinner mSystemSpinner;
    private int mSystem = InventoryContract.InventoryEntry.SYSTEM_UNKNOWN;

    private EditText mQuantityEditText;
    private Button mQuantityIncrease;
    private Button mQuantityDecrease;

    private RadioGroup mConditionRadioGroup;
    private RadioButton mConditionPoor;
    private RadioButton mConditionGood;
    private RadioButton mConditionGreat;
    private String mConditionSelected;

    private Boolean allValidData = false;
    private Boolean validGameName = false; private Boolean validGamePrice = false;
    private Boolean validGameQuantity = false; private Boolean validGameCondition = false;
    private Boolean barcodeSearched = false;

    private ImageButton scanBarcodeButton;
    private EditText mBarcodeEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initBarcodeScanning();
        initViews();
        setupSystemSpinner();


    }

    private void initBarcodeScanning() {
        scanBarcodeButton = findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(this);
        mBarcodeEditText = findViewById(R.id.barcode_entry_text);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            mBarcodeEditText.setText(scanContent);
        } else {
            Toast.makeText(getApplicationContext(), "No scan data recieved..", Toast.LENGTH_SHORT).show();
        }
        checkForNetwork();
    }

    private void checkForNetwork() {
        if (isNetworkAvailable()) {
            getDataRefresh();
        }
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectionCheck = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectionCheck.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected) {
            return true;
        } else {
            return false;
        }
    }

    public void getDataRefresh() {
        if(!barcodeSearched) {
            getLoaderManager().destroyLoader(SCAN_LOADER_ID);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(SCAN_LOADER_ID, null, this);
            barcodeSearched = true;
        }

    }

    private void setupSystemSpinner() {
        ArrayAdapter systemAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_system_types, android.R.layout.simple_spinner_dropdown_item);
        systemAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSystemSpinner.setAdapter(systemAdapter);

        mSystemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selectedItem)) {
                    if(selectedItem.equals(getString(R.string.sps3))) {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_PS3;
                    } else if (selectedItem.equals(getString(R.string.sps4))) {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_PS4;
                    } else if (selectedItem.equals(getString(R.string.mxbox))) {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_XBOXONE;
                    } else if (selectedItem.equals(getString(R.string.n3ds))) {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_N3DS;
                    } else if (selectedItem.equals(getString(R.string.nswitch))) {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_NSWITCH;
                    } else {
                        mSystem = InventoryContract.InventoryEntry.SYSTEM_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //Initialize views
    private void initViews() {
        mGameNameEditText = findViewById(R.id.inventory_item_add_edit_text);
        mGamePriceEditText = findViewById(R.id.game_price_edit_text);
        mGamePriceEditText.addTextChangedListener(new NumberTextWatcher(mGamePriceEditText, "##.##"));
        mBarcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                mBarcodeEditText.removeTextChangedListener(this);
                if (text !=null && !text.toString().isEmpty()) {
                    int stringLength;
                    stringLength = mBarcodeEditText.getText().length();
                    if (stringLength == 12) {
                        Toast.makeText(AddActivity.this, "Length is correct", Toast.LENGTH_SHORT).show();
                        getDataRefresh();
                    }
                }
                mBarcodeEditText.addTextChangedListener(this);
            }
        });
        mQuantityEditText = findViewById(R.id.inventory_quantity_edit_text);
        mQuantityIncrease = findViewById(R.id.inventory_qty_add);
        mQuantityIncrease.setOnClickListener(this);
        mQuantityDecrease = findViewById(R.id.inventory_qty_minus);
        mQuantityDecrease.setOnClickListener(this);
        mConditionRadioGroup = findViewById(R.id.condition_radio_group);
        mConditionPoor = findViewById(R.id.condition_poor);
        mConditionGood = findViewById(R.id.condition_good);
        mConditionGreat = findViewById(R.id.condition_great);
        mSystemSpinner = findViewById(R.id.inventory_system_spinner);

    }

    /*
    * Insert items into inventory and validate all data before reaching ContentProvider
    */
    private void insertInventory() {
        //Read data from EditText Inputs and add to inventory table
        String gamePrice = mGamePriceEditText.getText().toString().trim();
        String gameQuantity = mQuantityEditText.getText().toString().trim();
        String currentGameName = mGameNameEditText.getText().toString().toUpperCase().trim();
        //TODO: Setup game validation with boolean validGameName
        if(mGameNameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Must have a valid game..", Toast.LENGTH_SHORT).show();
        } else {
            validGameName = true;
        }

        //Validate pricing & format
        String[] currentSalePrice = mGamePriceEditText.getText().toString().split("$");
        Log.i(TAG, "insertInventory: value of pos 0:" + currentSalePrice[0]);
        String finalPrice = currentSalePrice[0].trim();
        if (mGamePriceEditText.getText().toString().trim().equalsIgnoreCase("")) {
            mGamePriceLayout.setError("Cannot be blank");
        } else if (!String.valueOf(finalPrice).matches(pricingPattern)) {
            mGamePriceEditText.setError("Must be valid price < $100");
        } else {
            //mSuggestedPrice = getSuggestedPrice(mGamePriceEditText.getText().toString());
            validGamePrice = true;
        }

        //Checks for valid system selection
        if (mSystem == InventoryContract.InventoryEntry.SYSTEM_UNKNOWN) {
            Toast.makeText(this, "Please select a valid Game System", Toast.LENGTH_SHORT).show();
        }

        //Checks for valid quantity
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        if (mQuantityEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Must be quantity greater than zero", Toast.LENGTH_SHORT).show();
        } else {
            validGameQuantity = true;
        }

        //Checks for valid game condition
        if (!mConditionGood.isChecked() && !mConditionPoor.isChecked() && !mConditionGreat.isChecked()) {
            Toast.makeText(this, "Game condition must be selected", Toast.LENGTH_SHORT).show();
        } else {
            switch (mConditionRadioGroup.getCheckedRadioButtonId()) {
                case R.id.condition_good:
                    mConditionSelected = "GOOD";
                case R.id.condition_poor:
                    mConditionSelected = "POOR";
                case R.id.condition_great:
                    mConditionSelected = "GREAT";
            }
            validGameCondition = true;
        }

        //Checks for all valid data
        if(validGameName && validGamePrice && validGameQuantity && validGameCondition) {
            mSuggestedPrice = getSuggestedPrice(mGamePriceEditText.getText().toString().trim());
            allValidData = true;
        }

        /*
        * Create ContentValues and add details from entry
        */
        if (allValidData) {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.GAME_NAME, currentGameName);
            values.put(InventoryContract.InventoryEntry.GAME_SALE_PRICE, gamePrice);
            values.put(InventoryContract.InventoryEntry.GAME_SYSTEM, mSystem);
            values.put(InventoryContract.InventoryEntry.GAME_QUANTITY, gameQuantity);
            values.put(InventoryContract.InventoryEntry.GAME_SUGGESTED_PRICE,mSuggestedPrice);
            values.put(InventoryContract.InventoryEntry.GAME_CONDITION, mConditionSelected);
            values.put(InventoryContract.InventoryEntry.GAME_UPC_CODE, mBarcodeEditText.getText().toString().trim());

            //TODO: If gameQuantity = zero, delete item instead of inserting/updating

            //Insert item into database through ContentProvider
            Uri gameInsertUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            if (gameInsertUri == null) {
                Toast.makeText(this, getString(R.string.editor_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_success), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Calculate suggested price based on system selection
    private String getSuggestedPrice(String trimPrice) {
        String finalPrice =trimPrice.replace("$","");
        Log.e(TAG, "getSuggestedPrice: final price is " + finalPrice);
        switch(mSystem) {
            case InventoryContract.InventoryEntry.SYSTEM_PS3:
                return format.format(Double.parseDouble(finalPrice) * 1.245);
            case InventoryContract.InventoryEntry.SYSTEM_PS4:
                return format.format(Double.parseDouble(finalPrice) * 1.802);
            case InventoryContract.InventoryEntry.SYSTEM_XBOXONE:
                return format.format(Double.parseDouble(finalPrice) * 1.802);
            case InventoryContract.InventoryEntry.SYSTEM_N3DS:
                return format.format(Double.parseDouble(finalPrice) * 1.612);
            case InventoryContract.InventoryEntry.SYSTEM_NSWITCH:
                return format.format(Double.parseDouble(finalPrice) * 1.802);
            default:
                return null;
        }
    }

    //Create Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the editor menu
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //Respond to the various menu options
            case R.id.action_save:
                //Save inventory item
                insertInventory();
                if (allValidData) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                //Delete current item
                //TODO Set up Delete Functionality
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.inventory_qty_add:
                mQuantityEditText.setText(increaseQuantity());
                break;
            case R.id.inventory_qty_minus:
                mQuantityEditText.setText(decreaseQuantity());
                break;
            case R.id.scan_barcode_button:
                if (!isNetworkAvailable()) {
                    Toast.makeText(this, "No Network Connection - Scan Not Allowed", Toast.LENGTH_SHORT).show();
                } else if (!mBarcodeEditText.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Barcode Already Exists - Scan Not Allowed", Toast.LENGTH_SHORT).show();
                } else {
                    IntentIntegrator scanIntegration = new IntentIntegrator(this);
                    scanIntegration.initiateScan();
                }
                break;
        }
    }

    /*
    * Increase quantity on hand and validate that quantity does not equal zero or above 1000
    */
    private String increaseQuantity() {
        int currentQuantity;
        String currentValue = mQuantityEditText.getText().toString();
        if (currentValue.equalsIgnoreCase("")) {
            currentQuantity = 0;
        } else {
            currentQuantity = Integer.parseInt(currentValue);
        }
        currentQuantity = currentQuantity + 1;
        if (currentQuantity >= 1000) {
            Toast.makeText(this, "Quantity over 1000 is a bit excessive...", Toast.LENGTH_SHORT).show();
        }
        return String.valueOf(currentQuantity);
    }

    /*
     * Decrease quantity on hand and validate that quantity does not equal zero
     */
    private String decreaseQuantity() {
        int currentQuantity;
        String currentValue = mQuantityEditText.getText().toString();
        currentQuantity = Integer.parseInt(currentValue);
        if (currentQuantity == 1) {
            Toast.makeText(this, "Cannot have inventory of zero - item will be deleted on saving", Toast.LENGTH_SHORT).show();
        }
        currentQuantity = currentQuantity - 1;
        return String.valueOf(currentQuantity);
    }

    @Override
    public Loader<List<upc>> onCreateLoader(int id, Bundle args) {
        String searchQuery = mBarcodeEditText.getText().toString();
        Uri baseUri = Uri.parse(requestUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("upc", searchQuery);

        return new upcLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<upc>> loader, List<upc> data) {
        if(!isNetworkAvailable()) {
            Toast.makeText(this, "Cannot query network", Toast.LENGTH_SHORT).show();
        }

        upc currentGame = data.get(0);
        String gameNameDetails = currentGame.getUpcGameName();
        String gameUPCDetails = currentGame.getUpcCode();
        String gameImageDetails = currentGame.getUpcGameImage();
        String gamePriceDetails = currentGame.getUpcGameLowPrice();
        mGameNameEditText.setText(gameNameDetails);
        mBarcodeEditText.setText(gameUPCDetails);
        mGamePriceEditText.setText(gamePriceDetails);
        mQuantityEditText.setText(increaseQuantity());

    }

    @Override
    public void onLoaderReset(Loader<List<upc>> loader) {

    }
}
