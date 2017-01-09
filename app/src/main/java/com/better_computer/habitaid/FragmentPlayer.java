package com.better_computer.habitaid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.form.schedule.NonSchedListAdapter;
import com.better_computer.habitaid.service.PlayerService;
import com.better_computer.habitaid.util.PlayerTask;

import java.util.List;

public class FragmentPlayer extends AbstractBaseFragment {

    protected NonSchedHelper nonSchedHelper;
//    protected volatile PlayerTask objCurPlayerTask;

    @Override
    public void refresh() {
    }

    public FragmentPlayer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_schedule_old_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);

        final List<NonSched> listPlayer = (List<NonSched>) (List<?>) nonSchedHelper.findBy("cat","player");

        final EditText etPlayerContent = ((EditText) rootView.findViewById(R.id.etPlayerContent));
        final EditText etAddName = ((EditText) rootView.findViewById(R.id.player_add_name));

        final ListView listView = ((ListView) rootView.findViewById(R.id.player_list));
        listView.setAdapter(new NonSchedListAdapter(context, listPlayer));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final NonSched nsPlayer = (NonSched) listView.getItemAtPosition(i);
                etAddName.setText(nsPlayer.getName());
                etPlayerContent.setText(nsPlayer.getContent());
            }
        });

        final ImageButton addButton = ((ImageButton) rootView.findViewById(R.id.player_add_btn));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NonSched nsPlayer = new NonSched();
                nsPlayer.setCat("player");

                //!!! need to eventually add a subcategory etc.
                nsPlayer.setName(etAddName.getText().toString());
                nsPlayer.setContent(etPlayerContent.getText().toString());

                if(DatabaseHelper.getInstance().getHelper(NonSchedHelper.class).createOrUpdate(nsPlayer)) {
                    Toast.makeText(context, "Saved.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Saving failed.", Toast.LENGTH_SHORT).show();
                }

                //setup_player(rootView);
            }
        });

        final ImageButton delButton = ((ImageButton) rootView.findViewById(R.id.player_del_btn));
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NonSched nsCurrent = (NonSched) nonSchedHelper.getBy("name",etAddName.getText().toString());
                Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show();
                nonSchedHelper.delete(nsCurrent.get_id());

                //setup_player(rootView);
            }
        });

        final Button btnSuper = ((Button) rootView.findViewById(R.id.btnSuper));
        btnSuper.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PlayerService.startService(context, etPlayerContent.getText().toString().split("\\n"), "SUPER");
//                objCurPlayerTask = new PlayerTask(context, etPlayerContent.getText().toString().split("\\n"), "SUPER");
//                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnHigh = ((Button) rootView.findViewById(R.id.btnHigh));
        btnHigh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PlayerService.startService(context, etPlayerContent.getText().toString().split("\\n"), "HIGH");
//                objCurPlayerTask = new PlayerTask(context, etPlayerContent.getText().toString().split("\\n"), "HIGH");
//                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnMedium = ((Button) rootView.findViewById(R.id.btnMedium));
        btnMedium.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PlayerService.startService(context, etPlayerContent.getText().toString().split("\\n"), "MEDIUM");
//                objCurPlayerTask = new PlayerTask(context, etPlayerContent.getText().toString().split("\\n"), "MEDIUM");
//                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnLow = ((Button) rootView.findViewById(R.id.btnLow));
        btnLow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PlayerService.startService(context, etPlayerContent.getText().toString().split("\\n"), "LOW");
//                objCurPlayerTask = new PlayerTask(context, etPlayerContent.getText().toString().split("\\n"), "LOW");
//                objCurPlayerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        final Button btnStopPlayer = ((Button) rootView.findViewById(R.id.btnStopFlashcards));
        btnStopPlayer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
//                if (objCurPlayerTask != null) {
//                    objCurPlayerTask.cancel(true);
//                }
                PlayerService.stopService(context);

                Toast.makeText(context, "thanks for playing", Toast.LENGTH_SHORT).show();
            }
        });
   }
}

