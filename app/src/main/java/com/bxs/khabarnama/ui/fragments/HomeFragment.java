package com.bxs.khabarnama.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.Constants;
import com.bxs.khabarnama.ui.Utils.Utilities;
import com.bxs.khabarnama.ui.adapters.AudioItemAdapter;
import com.bxs.khabarnama.ui.interfaces.InterfaceCommunicator;
import com.bxs.khabarnama.ui.models.AudioModel;
import com.bxs.khabarnama.ui.player.PlayerConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import static com.bxs.khabarnama.ui.Utils.Constants.CHANGE_BACKGROUND_AUDIO_INDEX;
import static com.bxs.khabarnama.ui.player.PlayerConstants.NEXT_AUDIO_INDEX;


public class HomeFragment extends Fragment {
    public final String TAG = HomeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerViewHeader header;
    private AudioItemAdapter mAudioItemAdapter;
    private SwipeRefreshLayout swipeContainer;
    public static ArrayList<AudioModel> audioModelArrayList = new ArrayList<>(1);
    //last position for change the selected item background
    private int mLastPosition;
    private InterfaceCommunicator mInterfaceCommunicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mInterfaceCommunicator = (InterfaceCommunicator) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement InterfaceCommunicator");
        }
    }

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


    }

    private void init(View rootView) {
        register_updateBackground();

        getViews(rootView);
        initializeSwipeRefresh();

        if (PlayerConstants.NEW_SONGS_LIST.size() <= 0) {
            PlayerConstants.NEW_SONGS_LIST = audioModelArrayList;
        }
    }

    private void initializeSwipeRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getViews(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleview);
        header = (RecyclerViewHeader) rootView.findViewById(R.id.header);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        header.attachTo(recyclerView);
    }

    private void register_updateBackground() {
        IntentFilter changeBackgroundFilter = new IntentFilter();
        changeBackgroundFilter.addAction(NEXT_AUDIO_INDEX);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getActivity());
        bm.registerReceiver(updateBackground, changeBackgroundFilter);

    }

    private BroadcastReceiver updateBackground = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NEXT_AUDIO_INDEX)) {
                if (getActivity() != null) {
                    final int index = intent.getIntExtra(CHANGE_BACKGROUND_AUDIO_INDEX, 1);
                    changeBackground(index);
                }
            }
        }
    };


    private void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new getList().execute();
                swipeContainer.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateBackground);
    }

    @Override
    public void onResume() {
        super.onResume();

        //get latest program
        new getList().execute();

    }


    private class getList extends AsyncTask<String, String, String> {
        int dpos = 0;
        int fpos = 0;
        String stName, fName, sturl, tmp;
        int stLength = 0;
        String d, m;
        String mDur, fsize;

        @Override
        protected String doInBackground(String... params) {
            try {
                publishProgress("start");

                String svrName = "http://dpr.radiokhabarnama.com";
                URL url = new URL(Constants.URL_NEW_PROGRAMS); // "http://dpr.radiokhabarnama.com" + "/    home/getlist?odays=10&passkey=7102"
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                String str = reader.readLine();
                if (str.charAt(0) != '$') {
                    publishProgress("err");
                    return "bye";
                } //return if File not starting with $ , i.e it is not our required file
                str = reader.readLine();
                PlayerConstants.NEW_SONGS_LIST.clear();
                stLength = str.length();
                while (stLength > 40) {

                    dpos = str.indexOf('$', 1);
                    mDur = str.substring(0, dpos);
                    dpos++;
                    fpos = str.indexOf('$', dpos);
                    fsize = str.substring(dpos, fpos);
                    dpos = str.indexOf('\\', 6);
                    dpos++;
                    fpos = str.indexOf('\\', dpos);
                    stName = str.substring(dpos, fpos);

                    fName = str.substring(fpos + 7, stLength - 4).trim();

                    m = Utilities.getMonth(str.substring(fpos + 3, fpos + 5));
                    d = str.substring(fpos + 5, fpos + 7);
                    tmp = svrName + "/" + str.substring(dpos - 7);
                    sturl = tmp.trim();
                    tmp = sturl.replace('\\', '/');
                    sturl = tmp.replaceAll(" ", "%20");


                    PlayerConstants.NEW_SONGS_LIST.add(new AudioModel(fName, stName, sturl, m, d, mDur, fsize)); //add to arraylist title,station name,file url,month,day,file duration in minutes,file size in kb

                    str = reader.readLine();
                    stLength = str.length();
                }

                reader.close();
            } catch (Exception e) {
                publishProgress("err");
            }

            publishProgress("ok");
            return "bye";
        }

        @Override
        protected void onProgressUpdate(String... params) {

            if (params[0].equals("start")) {
                Toast.makeText(getActivity(), "Fetching...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (params[0].equals("err")) {
                Toast.makeText(getActivity(), " Unable to connect Server, please try after few minutes", Toast.LENGTH_SHORT).show();
            }
//use here ArrayList to populate ListView in android activity


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mAudioItemAdapter = new AudioItemAdapter(getActivity(), PlayerConstants.NEW_SONGS_LIST);
            recyclerView.setAdapter(mAudioItemAdapter);
            audioModelArrayList = PlayerConstants.NEW_SONGS_LIST;
            mAudioItemAdapter.notifyDataSetChanged();

            mAudioItemAdapter.setOnItemClickListener(new AudioItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AudioModel item, int position) {
                    changeBackground(position);
                }
            });
        }


    }

    private void changeBackground(int position) {
        mInterfaceCommunicator.getResultBackFromDialog(position);
        mAudioItemAdapter.setSelectedPosition(position);
        mAudioItemAdapter.notifyItemChanged(position);
        mAudioItemAdapter.notifyItemChanged(mLastPosition);
        mLastPosition = position;
    }




}

