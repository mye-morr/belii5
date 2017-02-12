package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.better_computer.habitaid.MainActivity;
import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.Content;
import com.better_computer.habitaid.data.core.ContentHelper;

import java.util.List;

public class ContentListAdapter extends ArrayAdapter<Content> {

    private int resourceId;
    private List<Content> contents;
    private Context context;
    protected ContentHelper contentHelper;

    public ContentListAdapter(Context context, List<Content> contents) {
        super(context, R.layout.list_item_schedule, contents);
        this.context = context;
        this.contents = contents;
        this.resourceId = R.layout.list_item_schedule;
        this.contentHelper = DatabaseHelper.getInstance().getHelper(ContentHelper.class);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        final Content content = contents.get(position);

        convertView.setBackgroundColor(0x00000000);

        refreshView(convertView, content);

        return convertView;
    }

    private void refreshView(final View convertView, final Content content) {
        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(content.getContent());

        if(content.get_state().equalsIgnoreCase("active")) {
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single);
        }
        else{
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single_inactive);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //!!! implementation problem for inactive -> active
                if ("active".equals(content.get_state())) {
                    content.set_state("inactive");
                    ((MainActivity) context).dynaArray.removeArrayItem(content.getContent());

                    contentHelper.update(content);
                    refreshView(convertView, content);
                }
            }
        });
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