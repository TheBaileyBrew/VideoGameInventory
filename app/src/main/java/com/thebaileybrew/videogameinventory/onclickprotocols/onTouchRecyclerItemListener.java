package com.thebaileybrew.videogameinventory.onclickprotocols;

import android.support.v7.widget.RecyclerView;

public interface onTouchRecyclerItemListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
