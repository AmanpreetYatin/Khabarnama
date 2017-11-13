package com.bxs.khabarnama.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.Utilities;
import com.bxs.khabarnama.ui.player.PlayerConstants;

import static com.bxs.khabarnama.ui.Utils.Constants.PROGRESSBAR_CHANGE_KEY;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_BROADCAST;


public class PlayerActivity extends AppCompatActivity {

    static ImageView btnPause, btnBack, btnNext, btnPlay;

    static TextView textNowPlaying;

    static LinearLayout linearLayoutPlayer;
    SeekBar progressBar;
    static Context context;
    TextView textBufferDuration, textDuration;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_player);

        context = this;
        actionBar = getSupportActionBar();
        init();

    }
    private void init() {
        getViews();
        setListeners();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });

    }
    public static void changeUI() {
        updateUI();
        changeButton();
    }
    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    private static void updateUI() {
        try {
            String songName = PlayerConstants.NEW_SONGS_LIST.get(
                    PlayerConstants.SONG_NUMBER).getStRadioStName();

            textNowPlaying.setText(songName);

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void setListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
    private void sendIntent(int progress){

        Intent progressBarChangeFilter = new Intent(PROGRESS_BAR_BROADCAST);
        progressBarChangeFilter.putExtra(PROGRESSBAR_CHANGE_KEY,progress);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(progressBarChangeFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();



    }

    @Override
    protected void onResume() {
        super.onResume();


    }



}
