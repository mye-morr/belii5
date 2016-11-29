package com.better_computer.habitaid.form.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.core.Schedule;

public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private int resourceId;
    private List<Schedule> schedules;
    private Context context;

    public ScheduleListAdapter(Context context, List<Schedule> schedules) {
        super(context, R.layout.list_item_schedule, schedules);
        this.context = context;
        this.schedules = schedules;
        this.resourceId = R.layout.list_item_schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        Schedule sched = schedules.get(position);
        String sReceiver = "";

        if(sched.getCategory().equals("contacts")) {
            String sReceiverName = sched.getReceiverName();
            if(sReceiverName == null) {
                sReceiver += "(" + sched.getReceiver() + ") ";
            }
            else {
                sReceiver += "(" + sReceiverName + ") ";
            }
        }

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(sReceiver + sched.getMessage(65));

        if(sched.get_state().equalsIgnoreCase("active")) {
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single);
        }
        else{
            ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single_inactive);
        }

        if(sched.get_frame().equalsIgnoreCase("active")
        && sched.get_state().equalsIgnoreCase("inactive")) {
            convertView.setBackgroundColor(0x22000000);
        }
        else {
            convertView.setBackgroundColor(0x00000000);
        }
        return convertView;
    }

    private View initView(View convertView){
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return vi.inflate(resourceId, null);
        }
        else{
            return convertView;
        }
    }
}