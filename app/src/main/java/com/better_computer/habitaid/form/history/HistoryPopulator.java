package com.better_computer.habitaid.form.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.better_computer.habitaid.R;
import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.Message;
import com.better_computer.habitaid.data.core.MessageHelper;
import com.better_computer.habitaid.form.AbstractPopulator;

public class HistoryPopulator extends AbstractPopulator {

    public HistoryPopulator(Context context) {
        super(context);
    }

    @Override
    public void setup(View rootView, String category) {
        super.setup(rootView, category);
        List<Message> messages = (List<Message>)(List<?>) DatabaseHelper.getInstance().getHelper(MessageHelper.class).findAll();
        Collections.reverse(messages);
        ((ListView) rootView.findViewById(R.id.message_list)).setAdapter(new MessageListAdapter(context, messages));
    }

    public void setupClearHistory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear sent message history");
        builder.setMessage("Are you sure that you want to delete the sent message history?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MessageHelper messageHelper = DatabaseHelper.getInstance().getHelper(MessageHelper.class);
                List<SearchEntry> keys = new ArrayList<SearchEntry>();
                List<String> states = new ArrayList<String>();
                states.add("delivered");
                states.add("failed");
                keys.add(new SearchEntry(SearchEntry.Type.STRING, "_state", SearchEntry.Search.IN, states));
                messageHelper.delete(keys);
                resetup();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
