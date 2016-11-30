package com.better_computer.habitaid.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.better_computer.habitaid.R;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    Context mContext;
    int layoutResourceId;
    DrawerItem data[] = null;

    public DrawerAdapter(Context mContext, int layoutResourceId, DrawerItem[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.drawer_item_image);
        TextView textViewName = (TextView) listItem.findViewById(R.id.drawer_item_title);

        DrawerItem folder = data[position];

        imageViewIcon.setImageResource(folder.getIcon());
        textViewName.setText(folder.getName());

        return listItem;
    }
}
