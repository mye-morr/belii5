package com.better_computer.habitaid.player;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.better_computer.habitaid.MainActivity;
import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.Player;
import com.better_computer.habitaid.data.core.PlayerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedwei on 12/2/16.
 */

public class PlayerNamePickerFragment extends DialogFragment {

    private EditText subcatView;
    private EditText nameView;
    private SeekBar wtView;
    private Spinner spinner;
    private Listener listener;
    private List<NonSched> listPlayer;

    public interface Listener {
        void onValueSet(String subcat, String name, int wt);
    }

    public static PlayerNamePickerFragment newInstance() {
        PlayerNamePickerFragment fragment = new PlayerNamePickerFragment();
        return fragment;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_player_name_picker_dialog, container, false);

        subcatView = (EditText) rootView.findViewById(R.id.subcat);
        nameView = (EditText) rootView.findViewById(R.id.name);
        wtView = (SeekBar) rootView.findViewById(R.id.wt);

        rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subcat = subcatView.getText().toString();
                String name = nameView.getText().toString();
                int wt = wtView.getProgress() + 1;

                /*
                if (subcat.isEmpty() || name.isEmpty()) {
                    Toast.makeText(getActivity(), "Subcat or Name can not be empty.", Toast.LENGTH_SHORT);
                    return;
                }
                */

                if (listener != null) {
                    listener.onValueSet(subcat, name, wt);
                }

                dismiss();
            }
        });

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    // ignore
                    return;
                }

                if (listener != null) {
                    Player selectedItem = (Player)(listPlayer.get(position - 1));
                    subcatView.setText(selectedItem.getSubcat());
                    nameView.setText(selectedItem.getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        new MyTask().execute();

        return rootView;
    }

    private class MyTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            PlayerHelper playerHelper = DatabaseHelper.getInstance().getHelper(PlayerHelper.class);
            List<SearchEntry> keys = new ArrayList<SearchEntry>();
            listPlayer = playerHelper.find(keys, "Group BY subcat, name");
            List<String> displayList = new ArrayList<>();
            displayList.add(""); // add an empty item
            for (NonSched item : listPlayer) {
                String display = "(" + item.getSubcat() + ") - (" + item.getName() + ")";
                displayList.add(display);
            }
            return displayList;
        }

        @Override
        protected void onPostExecute(List<String> displayList) {
            super.onPostExecute(displayList);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, displayList);
            spinner.setAdapter(adapter);
        }
    }

}
