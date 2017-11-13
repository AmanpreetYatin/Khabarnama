package com.bxs.khabarnama.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.StorageUtil;
import com.bxs.khabarnama.ui.Utils.Utilities;
import com.bxs.khabarnama.ui.fragments.AboutusFragment;
import com.bxs.khabarnama.ui.fragments.HomeFragment;
import com.bxs.khabarnama.ui.interfaces.InterfaceCommunicator;
import com.bxs.khabarnama.ui.models.AudioModel;
import com.bxs.khabarnama.ui.player.PlayerConstants;
import com.bxs.khabarnama.ui.service.MediaPlayerService;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

import static com.bxs.khabarnama.ui.Utils.Constants.CHANGE_BACKGROUND_AUDIO_INDEX;
import static com.bxs.khabarnama.ui.Utils.Constants.PROGRESSBAR_CHANGE_KEY;
import static com.bxs.khabarnama.ui.Utils.Utilities.calculateProgress;
import static com.bxs.khabarnama.ui.Utils.Utilities.getDuration;
import static com.bxs.khabarnama.ui.player.PlayerConstants.NEXT_AUDIO_INDEX;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PLAY_PAUSE_ACTION;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_BROADCAST;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_HIDE;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_UPDATE;


public class MainActivity extends AppCompatActivity implements InterfaceCommunicator {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private Typeface fontTitle;
    private TextView tvTitle;
    private static TextView playingSong;
    private static LinearLayout llBottomPlayer;


    private ViewPagerAdapter adapter;
    private KProgressHUD mKProgressHUD;
    private static ImageView btnPause;
    private static ImageView btnBack;
    private static ImageView btnNext;
    private static ImageView btnPlay;
    private static ImageView btnPrevious;
    private SeekBar progressBar;
    private TextView textBufferDuration;
    private TextView textDuration;
    boolean serviceBound = false;
    private MediaPlayerService mSongService;
    private Handler mHandler = new Handler();
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.bxs.khabarnama.audioplayer.PlayNewAudio";
    private long maxDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fontTitle = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Medium.ttf");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mKProgressHUD = new KProgressHUD(MainActivity.this);
        mKProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Buffering...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        getViews();
        register_hideProgressBar();
        register_playPauseAction();
        register_updateProgressbar();

        setListener();
        llBottomPlayer.setVisibility(View.GONE);

    }

    private void getViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        playingSong = (TextView) findViewById(R.id.textNowPlaying);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnPause = (ImageView) findViewById(R.id.btnPause);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        btnPrevious = (ImageView) findViewById(R.id.btnPrevious);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        llBottomPlayer = (LinearLayout) findViewById(R.id.ll_bottom_player);
        tabLayout = (SmartTabLayout) findViewById(R.id.tabs);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);
        tvTitle.setTypeface(fontTitle);
        setupViewPager(viewPager);

        tabLayout.setViewPager(viewPager);
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
    }

    private void register_hideProgressBar() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRESS_BAR_HIDE);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(hideProgressBar, filter);


    }

    private void register_updateProgressbar() {
        IntentFilter progressBarUpdateFilter = new IntentFilter();
        progressBarUpdateFilter.addAction(PROGRESS_BAR_UPDATE);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(updateProgresssBar, progressBarUpdateFilter);

    }


    private void register_playPauseAction() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAY_PAUSE_ACTION);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(playPauseAction, filter);

    }


    private BroadcastReceiver updateProgresssBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_BAR_UPDATE)) {
                if (getApplicationContext() != null) {
                    final long progress = intent.getLongExtra("progressbar", 212214);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textBufferDuration.setText(Utilities.getDuration(progress));
                            int a = Utilities.calculateProgress(progress, maxDuration);
                            progressBar.setProgress(a);
                        }
                    });

                }
            }

        }
    };
    private BroadcastReceiver playPauseAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PLAY_PAUSE_ACTION)) {
                int param = intent.getIntExtra("playPaues", 1);
                if (param == 0) {
                    btnPlay.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.GONE);

                } else if (param == 1) {
                    btnPause.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.GONE);
                }

            }
        }
    };

    private BroadcastReceiver hideProgressBar = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mKProgressHUD.isShowing())
                mKProgressHUD.dismiss();

            final String tot = getDuration(MediaPlayerService.audioDuration);
            maxDuration = MediaPlayerService.audioDuration;
            progressBar.setMax(calculateProgress(maxDuration, maxDuration));
            textDuration.setText(tot);

        }
    };

    private void setListener() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MediaPlayerService().handleIncomingActions(new Intent(MediaPlayerService.ACTION_PLAY));
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);

            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MediaPlayerService().handleIncomingActions(new Intent(MediaPlayerService.ACTION_PAUSE));
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new MediaPlayerService().handleIncomingActions(new Intent(MediaPlayerService.ACTION_NEXT));
                mKProgressHUD.show();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AudioModel data = PlayerConstants.NEW_SONGS_LIST.get(MediaPlayerService.audioINdex);
                        playingSong.setText(data.getStRadioStName());
                    }
                }, 2000);


            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MediaPlayerService().handleIncomingActions(new Intent(MediaPlayerService.ACTION_PREVIOUS));
                mKProgressHUD.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AudioModel data = PlayerConstants.NEW_SONGS_LIST.get(MediaPlayerService.audioINdex);
                        playingSong.setText(data.getStRadioStName());
                    }
                }, 2000);
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

    private void sendIntent(int progress) {
        Intent progressBarChangeFilter = new Intent(PROGRESS_BAR_BROADCAST);
        progressBarChangeFilter.putExtra(PROGRESSBAR_CHANGE_KEY, progress);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(progressBarChangeFilter);
    }

    @Override
    public void getResultBackFromDialog(int position) {
        onItemClickListener(position);
    }

    private void onItemClickListener(int position) {
        if (Utilities.isConnectingToInternet(this)) {
            PlayerConstants.SONG_PAUSED = false;
            PlayerConstants.SONG_NUMBER = position;

            if (!serviceBound) {
                StorageUtil storage = new StorageUtil(this);
                storage.storeAudio(PlayerConstants.NEW_SONGS_LIST);
                storage.storeAudioIndex(position);
                Intent playerIntent = new Intent(this, MediaPlayerService.class);
                startService(playerIntent);
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                llBottomPlayer.setVisibility(View.VISIBLE);
                mKProgressHUD.show();

            } else {
                mKProgressHUD.show();
                //Store the new audioIndex to SharedPreferences
                StorageUtil storage = new StorageUtil(this);
                storage.storeAudioIndex(position);
                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                sendBroadcast(broadcastIntent);
                if (llBottomPlayer.getVisibility() == View.GONE)
                    llBottomPlayer.setVisibility(View.VISIBLE);

            }

            changeUI();


        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("deprecation")
    public static void updateUI() {
        try {
            AudioModel data = PlayerConstants.NEW_SONGS_LIST
                    .get(PlayerConstants.SONG_NUMBER);
            playingSong.setText(data.getStRadioStName());
            llBottomPlayer.setVisibility(View.VISIBLE);

        } catch (Exception e) {
        }
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

    public static void changeUI() {
        updateUI();
        changeButton();
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mSongService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            serviceBound = savedInstanceState.getBoolean("ServiceState");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Program");
        //  adapter.addFragment(new PodcastFragment(), "Podcast");
        //adapter.addFragment(new StoriesFragment(), "Story");
        adapter.addFragment(new AboutusFragment(), "About Us");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Khabarnama  provide  daily updated news about punjab. Check it here.\\nhttp://market.android.com/search?q=pname:com.bxs.khabarnama";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }


        if (id == R.id.action_rate_us) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.bxs.khabarnama")));
            return true;
        }
        if (id == R.id.action_privacy_policy) {
            new FinestWebView.Builder(this).show("http://dpr.radiokhabarnama.com/privacypolicy/privacypolicy.html");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else {

                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_alert_dialog)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MainActivity.super.onBackPressed();
                                if (mSongService != null)
                                    mSongService.stopService();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();



        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            mSongService.stopSelf();
            mHandler.removeCallbacksAndMessages(null);
            try {

                LocalBroadcastManager.getInstance(this).unregisterReceiver(playPauseAction);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(hideProgressBar);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(updateProgresssBar);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
