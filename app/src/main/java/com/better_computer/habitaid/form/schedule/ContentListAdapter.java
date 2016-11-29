package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.util.DynaArray;

public class ContentListAdapter extends ArrayAdapter<DynaArray.InternalItem> {

    private int resourceId;
    private DynaArray.InternalItem[] contents;
    private Context context;

    public ContentListAdapter(Context context, DynaArray.InternalItem[] contents) {
        super(context, R.layout.list_item_schedule, contents);
        this.context = context;
        this.contents = contents;
        this.resourceId = R.layout.list_item_schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        DynaArray.InternalItem content = contents[position];

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(content.getName());

        if(content.get_state().equalsIgnoreCase("active")) {
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single);
        }
        else{
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single_inactive);
        }

        convertView.setBackgroundColor(0x00000000);
        return convertView;
    }

    private View initView(View convertView){
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return vi.inflate(resourceId, null);
        }else{
            return convertView;
        }
    }
}