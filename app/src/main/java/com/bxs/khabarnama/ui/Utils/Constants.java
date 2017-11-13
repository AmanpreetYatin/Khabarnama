package com.bxs.khabarnama.ui.Utils;

import android.media.MediaPlayer;



public class Constants {
    private static String BASE_URL  =  "http://loksatth.khabarnama.it/khabarnama/index.php";
    public static String URL_LATEST_AUDIIO = BASE_URL + "/stud";
    public static String URL_PODCAST = BASE_URL + "/podcast";
    public static String URL_STORY = BASE_URL + "/story";

    public static String URL_NEW_PROGRAMS = "http://dpr.radiokhabarnama.com/home/getlist?odays=8&passkey=7102";
    public static String URL_PANU_PROGRAMS = "http://dpr.radiokhabarnama.com/aman/getFiles.php";



    public static final String PREFS = "prefs";
    public static final String REGID = "regId";
    public static final String FCMID = "fcm_id";
    public static final String PLAYING = "playing";
    public static final String PROGRESSBAR_CHANGE_KEY = "progressKey";
    public static final String CHANGE_BACKGROUND_AUDIO_INDEX = "changeBackgroundAudioindex";




    public static int playerIndex = -1;
    public static MediaPlayer mediaPlayer;
    public static int mediaPosition = -1;

    //gcm register url
    public static String GCM_REGISTER = "http://loksatth.khabarnama.it/admin/register_device.php";
    public static final String NO_INTERNET_MESSAGE = "no internet connection!";
    public static final String DOWNLOADING_MESSAGE = "Downloading...";

}
