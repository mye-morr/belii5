package com.better_computer.habitaid.util;

/**
 * Created by tedwei on 11/24/16.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}

