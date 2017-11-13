package com.bxs.khabarnama.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.StorageUtil;
import com.bxs.khabarnama.ui.Utils.Utilities;
import com.bxs.khabarnama.ui.models.AudioModel;
import com.bxs.khabarnama.ui.player.PlayerConstants;
import com.bxs.khabarnama.ui.service.MediaPlayerService;

import java.util.ArrayList;

import static com.bxs.khabarnama.ui.Utils.Constants.PROGRESSBAR_CHANGE_KEY;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_BROADCAST;


public class PlayerActivitynew extends AppCompatActivity implements View.OnClickListener {

    static ImageView btnBack;

    static TextView textNowPlaying;

    static LinearLayout linearLayoutPlayer;
    static Context context;
    ActionBar actionBar;
    private SharedPreferences preferences;
    private LinearLayout llNoInternet;
    private SwipeRefreshLayout swipeContainer;
    public static ArrayList<AudioModel> audioModelArrayList = new ArrayList<>(1);

    private SeekBar progressBar;
    private TextView textBufferDuration, textDuration;
    private static ImageView imageViewAlbumArt, btnPrevious, btnPause, btnPlay,
            btnNext, btnPlayer, btnStop;
    private static TextView playingSong;
    private static LinearLayout linearLayoutPlayingSong;
    //new player
    private MediaPlayerService player;
    boolean serviceBound = false;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.bxs.khabarnama.audioplayer.PlayNewAudio";

    int audioIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player);

        context = this;
        actionBar = getSupportActionBar();
        init();
        audioIndex = getIntent().getIntExtra("audioIndex",0);

    }

    private void init() {
        getViews();
        setListeners();
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        PlayerConstants.PROGRESSBAR_HANDLER = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Integer i[] = (Integer[]) msg.obj;
                textBufferDuration.setText(Utilities.getDuration(i[0]));
                textDuration.setText(Utilities.getDuration(i[1]));
                progressBar.setProgress(i[2]);
            }
        };
    }


    private void getViews() {
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnPause = (ImageView) findViewById(R.id.btnPause);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        textNowPlaying = (TextView) findViewById(R.id.textNowPlaying);
        linearLayoutPlayer = (LinearLayout) findViewById(R.id.linearLayoutPlayer);

        progressBar = (SeekBar) findViewById(R.id.progressBar);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);
        textNowPlaying.setSelected(true);

    }


    private void setListeners(){
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playAudio(audioIndex);
//              //  changeButton();
//                btnPause.setVisibility(View.VISIBLE);
//                btnPlay.setVisibility(View.GONE);
//            }
//        });
        btnPause.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MediaPlayerService().pauseMedia();
//                 //new MediaPlayerService().handleIncomingActions(new Intent(MediaPlayerService.ACTION_PAUSE));
//                  btnPause.setVisibility(View.GONE);
//                  btnPlay.setVisibility(View.VISIBLE);
//                 }
//        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new MediaPlayerService().skipToNext(getApplicationContext());
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new MediaPlayerService().skipToPrevious(getApplicationContext());
            }
        });
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent i = new Intent(getActivity(),
////                        SongService.class);
////                stopService(i);
////                linearLayoutPlayingSong.setVisibility(View.GONE);
//            }
//        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progresss = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                sendIntent(progresss);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                progresss = progress;

            }

        });

    }
    public static void changeButton() {
        if (new MediaPlayerService().mediaPlayer.isPlaying()) {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        } else {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }
    }
    private void sendIntent(int progress){

        Intent progressBarChangeFilter = new Intent(PROGRESS_BAR_BROADCAST);
        progressBarChangeFilter.putExtra(PROGRESSBAR_CHANGE_KEY,progress);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(progressBarChangeFilter);
    }
    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(getApplicationContext(), "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(PlayerConstants.NEW_SONGS_LIST);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            serviceBound = savedInstanceState.getBoolean("ServiceState");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        if (MediaPlayerService.mediaPlayer == null)
        {
            playAudio(audioIndex);
            btnPause.setVisibility(view.VISIBLE);
            btnPlay.setVisibility(View.GONE);

        }else {
            if (MediaPlayerService.mediaPlayer != null) {
                if (MediaPlayerService.mediaPlayer.isPlaying()) {
                    new MediaPlayerService().pauseMedia(getApplicationContext());
                    btnPlay.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);
                } else {
                    new MediaPlayerService().resumeMedia();
                    btnPause.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.GONE);
                }

            }
        }
    }
}
