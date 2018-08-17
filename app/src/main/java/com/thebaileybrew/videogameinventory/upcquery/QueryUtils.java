package com.thebaileybrew.videogameinventory.upcquery;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class QueryUtils {
    private static final String TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {}

    public static List<upc> fetchGameDetails(String requestUrl) {
        URL apiUrl = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpsRequest(apiUrl);
        } catch (IOException ioe) {
            Log.e(TAG, "fetchGameDetails: Problem with HTTPS request", ioe);
        }

        List<upc> upcDetails = extractDetailsFromJson(jsonResponse);
        Log.v("Fetch", "Yes");
        return upcDetails;
    }

    private static URL createUrl (String apiUrl) {
        URL url = null;
        try {
            url = new URL(apiUrl);
        } catch (MalformedURLException mue) {
            Log.e(TAG, "createUrl: Problem building URL", mue);
        }
        return url;
    }

    private static String makeHttpsRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(16000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "makeHttpsRequest: Error Code" + urlConnection.getResponseCode());
            }
        } catch (IOException ioe) {
            Log.e(TAG, "makeHttpsRequest: Could not retrieve JSON", ioe);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<upc> extractDetailsFromJson(String jsonData) {
        String upcCode;
        String upcGameName;
        String upcGameImage;
        String upcGamePrice;

        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }

        List<upc> upcDetails = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonData);
            Log.v("JSON ARRAY", "Json Data");
            JSONArray currentGameArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < currentGameArray.length(); i++) {
                JSONObject currentGame = currentGameArray.getJSONObject(i);
                upcCode = currentGame.getString("upc");
                upcGameName = currentGame.getString("title");
                upcGamePrice = currentGame.getString("lowest_recorded_price");
                JSONArray imageArray = currentGame.getJSONArray("images");
                Log.e(TAG, "extractDetailsFromJson: " + imageArray.length() );
                if(imageArray.length() == 0) {
                    upcGameImage = null;
                } else {
                    JSONObject currentImage = imageArray.getJSONObject(0);
                    upcGameImage = currentImage.getString("0");
                }
                upcDetails.add(new upc(upcCode, upcGameName, upcGameImage, upcGamePrice));
            }
        } catch (JSONException je) {
            Log.e(TAG, "extractDetailsFromJson: Problems ", je);
        }
        return upcDetails;
    }
}
