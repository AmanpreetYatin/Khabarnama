package com.bxs.khabarnama.ui.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.PlaybackStatus;
import com.bxs.khabarnama.ui.Utils.StorageUtil;
import com.bxs.khabarnama.ui.activities.MainActivity;
import com.bxs.khabarnama.ui.app.MyApplication;
import com.bxs.khabarnama.ui.models.AudioModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.bxs.khabarnama.ui.Utils.Constants.CHANGE_BACKGROUND_AUDIO_INDEX;
import static com.bxs.khabarnama.ui.Utils.Constants.PROGRESSBAR_CHANGE_KEY;
import static com.bxs.khabarnama.ui.player.PlayerConstants.NEXT_AUDIO_INDEX;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PLAY_PAUSE_ACTION;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_BROADCAST;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_HIDE;
import static com.bxs.khabarnama.ui.player.PlayerConstants.PROGRESS_BAR_UPDATE;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,AudioManager.OnAudioFocusChangeListener{

     private static final String TAG = MediaPlayerService.class.getName();
    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();
    public static MediaPlayer mediaPlayer;
    //Used to pause/resume MediaPlayer
    private static int resumePosition;
    //path to the audio file
    private String mediaFile;
    private AudioManager audioManager;
    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    //The system calls this method when an activity, requests the service be started
    //List of available Audio files
    private static ArrayList<AudioModel> audioList;
    private static int audioIndex = -1;
    private static AudioModel activeAudio; //an object of the currently playing audio
    public static final String ACTION_PLAY = "com.bxs.khabarnama.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.bxs.khabarnama.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.bxs.khabarnama.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.bxs.khabarnama.audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.bxs.khabarnama.audioplayer.ACTION_STOP";
    //MediaSession
  //  private static MediaSessionManager mediaSessionManager;
    private static MediaSessionCompat mediaSession;
    private static MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;
    public static long audioDuration;
    public static String audioName = "Radio Khabarnama";
    private static Timer timer;
    public static int audioINdex;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSession == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }

                    buildNotification(PlaybackStatus.PLAYING);


        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return START_NOT_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRESS_BAR_BROADCAST);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);
        timer = new Timer();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
    }
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        //mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            //mediaPlayer.setDataSource(mediaFile);
            mediaPlayer.setDataSource(activeAudio.getUrlName());


        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();


    }
    public void playMedia(Context context) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            buildNotification(PlaybackStatus.PLAYING);


            audioDuration =  mediaPlayer.getDuration();


            timer.scheduleAtFixedRate(new MainTask(), 0, 100);
           // sendIntent(mediaPlayer.getCurrentPosition());
            sendIntent();

        }
    }
    private class MainTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer != null) {
               try {
                long progress = mediaPlayer.getCurrentPosition();
                broadCast(progress);
                } catch (Exception e) {
                    Log.v(TAG,e.getMessage(),e);
                }
            }
        }
    };
    private void broadCast(long progresss){
        Intent sendProgress = new Intent(PROGRESS_BAR_UPDATE);
        sendProgress.putExtra("progressbar",progresss);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(sendProgress);
    }
    private void sendIntent() {
        Intent progressBarhide = new Intent(PROGRESS_BAR_HIDE);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(progressBarhide);
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia(Context context) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            buildNotification(PlaybackStatus.PAUSED);
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();

            buildNotification(PlaybackStatus.PLAYING);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        stopMedia();
        //stop the service
        stopSelf();
    }


    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.

        playMedia(MyApplication.getMyApplicationInstance());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
       /*     case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                Toast.makeText(getApplicationContext(),"focus gain",Toast.LENGTH_SHORT).show();
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Toast.makeText(getApplicationContext(),"focus loss",Toast.LENGTH_SHORT).show();
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;*/
           case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
              if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia(getApplicationContext());
            buildNotification(PlaybackStatus.PAUSED);
        }
    };
    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }
    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer


                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                pauseMedia(getApplicationContext());
                                ongoingCall = true;
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                             if (ongoingCall) {
                            ongoingCall = false;
                            resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();

            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };
    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
    public void stopService(){
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaSession = null;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
         removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(mBroadcastReceiver);

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }
    // handler for received data from service
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_BAR_BROADCAST)) {
                int param = intent.getIntExtra(PROGRESSBAR_CHANGE_KEY, 1);

                mediaPlayer.seekTo(param * mediaPlayer.getDuration() / 100);
            }
        }
    };
    private void initMediaSession() throws RemoteException {
        if (mediaSession != null) return; //mediaSessionManager exists

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          //  mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        //}else {

        //    final Context context = MyApplication.getMyApplicationInstance();

         //   mediaSessionManager = (MediaSessionManager) context
              //      .getSystemService(Context.MEDIA_SESSION_SERVICE);


      //  }

        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");

        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();

        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();


                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia(getApplicationContext());
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext(getApplicationContext());
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious(getApplicationContext());
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_noti); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getSongTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getStRadioStName())
                .build());


    }
    public void skipToNext(Context context) {

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }
        audioINdex = audioIndex;
        //Update stored index
        new StorageUtil(context).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
        audioName = activeAudio.getStRadioStName();

        sendAudioindexForChangebackground(audioIndex);
    }
    private void sendAudioindexForChangebackground(int audioIndex){
        Intent intent = new Intent(NEXT_AUDIO_INDEX);
        intent.putExtra(CHANGE_BACKGROUND_AUDIO_INDEX,audioIndex);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(intent);
    }


    public void skipToPrevious(Context context) {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }
        audioINdex = audioIndex;
        //Update stored index
        new StorageUtil(context).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();

        sendAudioindexForChangebackground(audioIndex);
    }
    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentMainActivity = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
            sendIntentPlayPause(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
            sendIntentPlayPause(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(MyApplication.getMyApplicationInstance().getResources(),
                R.drawable.player_back1); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentTitle(activeAudio.getStRadioStName())
                .setContentText(activeAudio.getSongTitle())
                .setContentIntent(pendingIntentMainActivity)
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

              ((NotificationManager) MyApplication.getMyApplicationInstance().getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }
    //plapause = 0 play , playpause = 1 pause
    private void sendIntentPlayPause(int playpause) {
        Intent playPauseFilter = new Intent(PLAY_PAUSE_ACTION);
        playPauseFilter.putExtra("playPaues", playpause);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(playPauseFilter);
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    public PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(MyApplication.getMyApplicationInstance(), MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(MyApplication.getMyApplicationInstance(), actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(MyApplication.getMyApplicationInstance(), actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(MyApplication.getMyApplicationInstance(), actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(MyApplication.getMyApplicationInstance(), actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }
    public void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }
}
