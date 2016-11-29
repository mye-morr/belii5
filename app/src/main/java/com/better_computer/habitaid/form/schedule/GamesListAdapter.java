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
import com.better_computer.habitaid.data.core.Games;

public class GamesListAdapter extends ArrayAdapter<Games> {

    private int resourceId;
    private List<Games> games;
    private Context context;

    public GamesListAdapter(Context context, List<Games> games) {
        super(context, R.layout.list_item_schedule, games);
        this.context = context;
        this.games = games;
        this.resourceId = R.layout.list_item_schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView);

        Games game = games.get(position);
        String sCat = game.getCat();
        String sPts = game.getPts();

        if (sCat.length() > 0) {
            sCat = sCat + ": ";
        }

        if (sPts.length() > 0) {
            sPts = " (" + sPts + ")";
        }

        ((TextView) convertView.findViewById(R.id.schedule_item_summary)).setText(sCat + game.getContent() + sPts);

            if(games.get(position).get_state().equalsIgnoreCase("active")) {
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