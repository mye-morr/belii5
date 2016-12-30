package com.better_computer.habitaid;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.better_computer.habitaid.data.DatabaseHelper;
import com.better_computer.habitaid.data.SearchEntry;
import com.better_computer.habitaid.data.core.NonSched;
import com.better_computer.habitaid.data.core.NonSchedHelper;
import com.better_computer.habitaid.data.core.ScheduleHelper;
import com.better_computer.habitaid.util.MarginDecoration;
import com.better_computer.habitaid.util.StopwatchUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FragmentFbIntegrate extends AbstractBaseFragment {

    protected ScheduleHelper scheduleHelper;
    private Handler uiHander;

    private TextView stopwatchView;

    @Override
    public void refresh() {
    }

    public FragmentFbIntegrate() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fb_integrate, container, false);

        stopwatchView = (TextView) rootView.findViewById(R.id.stopwatch);

        rootView.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopwatchUtil.resetStopwatchStartTime(context);
                if (uiHander == null) {
                    uiHander = new Handler();
                }
                uiHander.post(updateStopwatchRunnable);
                stopwatchView.setText("");
            }
        });

        rootView.findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiHander = null;
                if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    StopwatchUtil.setStopwatchStopTime(context, System.currentTimeMillis());

                    long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                    long passedSeconds = passedTime / 1000;
                    String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                    stopwatchView.setText(strPassedTime);
                }
            }
        });

        rootView.findViewById(R.id.sync_2_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Sync2ClientTask().execute();
            }
        });

        rootView.findViewById(R.id.sync_2_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Sync2ServerTask().execute();
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.squares);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView.setAdapter(new SquaresAdapter());

        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        this.scheduleHelper = DatabaseHelper.getInstance().getHelper(ScheduleHelper.class);

        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        Fragment fragment = ((MainActivity)context).getSupportFragmentManager().findFragmentById(R.id.container);
        loginButton.setFragment(fragment);

        final View postView = rootView.findViewById(R.id.post_button);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl (Uri.parse("http://www.google.com"))
                        .setContentTitle("Hello Facebook")
                        .setContentDescription(
                                "The 'Hello Facebook' sample showcases simple Facebook integration")
                        .build();
                ShareDialog shareDialog = new ShareDialog((MainActivity)context);
                shareDialog.show(linkContent);
            }
        });

        final View postLayoutView = rootView.findViewById(R.id.post_layout);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            // user has logged in
            postLayoutView.setVisibility(View.VISIBLE);
        }

        CallbackManager callbackManager = ((MyApplication)((MainActivity) context).getApplication()).getCallbackManager();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Test", "onSuccess");
                postLayoutView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.d("Test", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Test", "onError");
            }
        });
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CallbackManager callbackManager = ((com.better_computer.habitaid.MyApplication)getActivity().getApplication()).getCallbackManager();
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StopwatchUtil.getStopwatchStopTime(context) < 0) {
            // is running
            if (uiHander == null) {
                uiHander = new Handler();
            }
            uiHander.post(updateStopwatchRunnable);
        } else {
            updateStopwatchRunnable.run();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHander = null;
    }

    private Runnable updateStopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                /* we don't want to see the elapsed time until end
                long passedTime = StopwatchUtil.getStopwatchPassedTime(context);
                long passedSeconds = passedTime / 1000;
                String strPassedTime = String.format("%d:%02d", passedSeconds / 60, passedSeconds % 60);
                stopwatchView.setText(strPassedTime);
                */
            } finally {
                if (uiHander != null && StopwatchUtil.getStopwatchStopTime(context) < 0) {
                    uiHander.postDelayed(updateStopwatchRunnable, 1000L);
                }
            }
        }
    };

    class SquaresAdapter extends RecyclerView.Adapter<SquaresAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_square, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return 16;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView text1View;
            TextView text2View;

            public ViewHolder(View itemView) {
                super(itemView);
                text1View = (TextView) itemView.findViewById(R.id.text1);
                text2View = (TextView) itemView.findViewById(R.id.text2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence[] items = new CharSequence[] {text1View.getText(), text2View.getText()};
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setItems(items, null);
                        alertDialog.setCancelable(true);
                        alertDialog.show();
                    }
                });
            }

            void setData(int data) {
                text1View.setText("text1-" + data);
                text2View.setText("text2-" + data);
            }
        }

    }

    private Connection openConnection() {
        String ipaddress = "184.168.194.77";
        String db = "narfdaddy2";
        String username = "narfdaddy2";
        String password = "TreeDemo1";

        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ipaddress + ";"

                    + "databaseName=" + db + ";user=" + username
                    + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
            return connection;
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }

        return null;
    }

    private class Sync2ServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            List<NonSched> list = nonSchedHelper.findAll();

            Connection connection = null;
            PreparedStatement stmt = null;
            try {
                connection = openConnection();
                connection.setAutoCommit(false);

                String sql = "DELETE FROM core_tbl_nonsched";
                stmt = connection.prepareStatement(sql);
                stmt.execute();

                sql = "INSERT INTO core_tbl_nonsched (_id ,_frame ,_state ,cat ,subcat ,subsub ,iprio ,name ,abbrev ,content ,notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = connection.prepareStatement(sql);
                for (NonSched nonSched : list) {
                    stmt.setString(1, nonSched.get_id());
                    stmt.setString(2, nonSched.get_frame());
                    stmt.setString(3, nonSched.get_state());
                    stmt.setString(4, nonSched.getCat());
                    stmt.setString(5, nonSched.getSubcat());
                    stmt.setString(6, nonSched.getSubsub());
                    stmt.setString(7, nonSched.getIprio());
                    stmt.setString(8, nonSched.getName());
                    stmt.setString(9, nonSched.getAbbrev());
                    stmt.setString(10, nonSched.getContent());
                    stmt.setString(11, nonSched.getNotes());
                    stmt.executeUpdate();
                }

                connection.commit();
            } catch (Exception ex) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class Sync2ClientTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NonSchedHelper nonSchedHelper = DatabaseHelper.getInstance().getHelper(NonSchedHelper.class);
            nonSchedHelper.delete(new ArrayList<SearchEntry>());    // delete all

            Connection connection = null;
            Statement stmt = null;
            try {
                connection = openConnection();

                String sql = "SELECT * FROM core_tbl_nonsched";
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    NonSched nonSched = new NonSched();
                    nonSched.set_id(rs.getString("_id"));
                    nonSched.set_frame(rs.getString("_frame"));
                    nonSched.set_state(rs.getString("_state"));
                    nonSched.setCat(rs.getString("cat"));
                    nonSched.setSubcat(rs.getString("subcat"));
                    nonSched.setSubsub(rs.getString("subsub"));
                    nonSched.setIprio(rs.getString("iprio"));
                    nonSched.setName(rs.getString("name"));
                    nonSched.setAbbrev(rs.getString("abbrev"));
                    nonSched.setContent(rs.getString("content"));
                    nonSched.setNotes(rs.getString("notes"));
                    nonSchedHelper.create(nonSched);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}