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

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.Player;
import com.better_computer.habitaid.data.core.PlayerHelper;

import java.util.ArrayList;
import java.util.List;

public class ContentExtPickerFragment extends DialogFragment {

    private SeekBar wtView;
    private SeekBar extpctView;
    private SeekBar extthrView;
    private Listener listener;
    private List<NonSched> listPlayer;

    public interface Listener {
        void onValueSet(int wt, double extpct, double extthr);
    }

    public static ContentExtPickerFragment newInstance() {
        ContentExtPickerFragment fragment = new ContentExtPickerFragment();
        return fragment;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_content_ext_dialog, container, false);

        wtView = (SeekBar) rootView.findViewById(R.id.wt);
        extpctView = (SeekBar) rootView.findViewById(R.id.extpct);
        extthrView = (SeekBar) rootView.findViewById(R.id.extthr);

        rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        rootView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int wt = wtView.getProgress() + 1;
                double extpct = (extpctView.getProgress() + 1) / 10;
                double extthr = (extthrView.getProgress() + 1) / 10;

                if (listener != null) {
                    listener.onValueSet(wt, extpct, extthr);
                }

                dismiss();
            }
        });

        return rootView;
    }
}
