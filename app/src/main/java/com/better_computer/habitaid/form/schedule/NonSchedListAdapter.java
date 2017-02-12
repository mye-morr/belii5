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
import com.better_computer.habitaid.data.core.NonSched;

public class NonSchedListAdapter extends ArrayAdapter<NonSched> {

    private int resourceId;
    private List<NonSched> nonSched;
    private Context context;

    public NonSchedListAdapter(Context context, List<NonSched> nonSched) {
        super(context, R.layout.list_item_schedule, nonSched);
        this.context = context;
        this.nonSched = nonSched;
        this.resourceId = R.layout.list_item_schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        String sLabel = "";
        String nsCat = nonSched.get(position).getCat();

        // see NewWizardDialog: DialogStep_1Step_Events
        if(nsCat.equalsIgnoreCase("PLAYER")) {
            sLabel = nonSched.get(position).getName();
        }
        else {
            sLabel = nonSched.get(position).getName()
                    + " -= "
                    + nonSched.get(position).getContent();
        }

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(sLabel);

            if(nonSched.get(position).get_state().equalsIgnoreCase("active")) {
                ((ImageView) convertView.findViewById(R.id.schedule_item_icon)).setImageResource(R.drawable.schedule_single);
            }else{
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