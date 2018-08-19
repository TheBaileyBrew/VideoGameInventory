package com.thebaileybrew.videogameinventory.interfacefiles;

import android.view.View;

public interface CustomOnClickInterface {
    void onItemClick(View v, int position);

    void onLongClick(View v, String rowId);
}
