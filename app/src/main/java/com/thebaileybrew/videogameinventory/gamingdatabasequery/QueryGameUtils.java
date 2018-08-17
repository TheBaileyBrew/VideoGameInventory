package com.thebaileybrew.videogameinventory.gamingdatabasequery;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thebaileybrew.videogameinventory.database.InventoryContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class QueryGameUtils {

    private static final String TAG = QueryGameUtils.class.getSimpleName();

    private QueryGameUtils() {}

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("gamesdb.json");
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputReader = new InputStreamReader(inputStream,
                        Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
                json = output.toString();
                inputStream.close();
            }
        } catch (IOException iex) {
            iex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void extractDataFromJson(String jsonString, Context context) {
        ContentValues values = new ContentValues();
        String gameName;
        String gameDeveloper;
        String gameDevCountry;
        String gameDevPhone;
        String gameDevEstablished;
        String gameGenre;
        String gameReleased;
        String gameSystem;

        if (TextUtils.isEmpty(jsonString)) {
            Toast.makeText(context.getApplicationContext(),
                    "No Data to Load", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject baseJSONResponse = new JSONObject(jsonString);
            JSONArray baseJSONArray = baseJSONResponse.getJSONArray("games");
            for (int g = 0; g < baseJSONArray.length(); g++) {
                JSONObject currentGame = baseJSONArray.getJSONObject(g);
                gameName = currentGame.getString("TITLE");
                gameDeveloper = currentGame.getString("DEVELOPER");
                //check for Dev Country
                if (currentGame.getString("DEVHOMECOUNTRY").isEmpty()) {
                    gameDevCountry = "Developer Country Unknown";
                } else {
                    gameDevCountry = currentGame.getString("DEVHOMECOUNTRY");
                }
                gameDevPhone = currentGame.getString("DEVPHONE");
                //check for Dev Establish Date
                if (currentGame.getString("DEVESTABLISHED").isEmpty()) {
                    gameDevEstablished = "Developer Establish Date Unknown";
                } else {
                    gameDevEstablished = currentGame.getString("DEVESTABLISHED");
                }
                gameGenre = currentGame.getString("GENRE");
                gameReleased = currentGame.getString("RELEASEDATE");
                gameSystem = currentGame.getString("SYSTEM");

                //Add values to Game DB
                values.put(InventoryContract.GameEntry.GAME_NAME, gameName);
                values.put(InventoryContract.GameEntry.GAME_GENRE, gameGenre);
                values.put(InventoryContract.GameEntry.GAME_SYSTEM, gameSystem);
                values.put(InventoryContract.GameEntry.GAME_RELEASE_DATE, gameReleased);
                values.put(InventoryContract.GameEntry.GAME_DEVELOPER, gameDeveloper);
                values.put(InventoryContract.GameEntry.GAME_DEV_COUNTRY, gameDevCountry);
                values.put(InventoryContract.GameEntry.GAME_DEV_HQ, gameDevPhone);
                values.put(InventoryContract.GameEntry.GAME_DEV_ESTB, gameDevEstablished);
                Uri gameInsertDBUri = context.getContentResolver().insert(
                        InventoryContract.GameEntry.CONTENT_URI, values);
                if (gameInsertDBUri == null) {
                    Toast.makeText(context.getApplicationContext(),
                            "Failed to add to DB", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException je) {
            Log.e(TAG, "extractDataFromJson: Got 99Problems", je);
        }


    }
}
