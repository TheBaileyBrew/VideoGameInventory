package com.thebaileybrew.videogameinventory.upcquery;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class upcLoader extends AsyncTaskLoader<List<upc>> {

    private String mUrl;

    public upcLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

        @Override
        protected void onStartLoading() {
        Log.v("on start loader", "yes");
        forceLoad();
    }

        @Override
        public List<upc> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<upc> upcDetails = QueryUtils.fetchGameDetails(mUrl);
        Log.v("Loading in BG", "yes");
        return upcDetails;
    }

}
