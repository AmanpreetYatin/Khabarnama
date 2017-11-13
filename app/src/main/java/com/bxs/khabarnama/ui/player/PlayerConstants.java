package com.bxs.khabarnama.ui.player;

import android.os.Handler;


import com.bxs.khabarnama.ui.models.AudioModel;

import java.util.ArrayList;

public class PlayerConstants {
    //List of Songs
    public static ArrayList<AudioModel> SONGS_LIST = new ArrayList<AudioModel>();
    public static ArrayList<AudioModel> NEW_SONGS_LIST = new ArrayList<AudioModel>();
    //song number which is playing right now from SONGS_LIST
    public static int SONG_NUMBER = 0;
    //song is playing or paused
    public static boolean SONG_PAUSED = true;
    //song changed (next, previous)
    public static boolean SONG_CHANGED = false;
    //handler for song changed(next, previous) defined in service(SongService)
    public static Handler SONG_CHANGE_HANDLER;
    //handler for song play/pause defined in service(SongService)
    public static Handler PLAY_PAUSE_HANDLER;
    //handler for showing song progress defined in Activities(MainActivity, AudioPlayerActivity)
    public static Handler PROGRESSBAR_HANDLER;

    public static final String PROGRESS_BAR_BROADCAST = "progress.change";
    public static final String PROGRESS_BAR_HIDE = "progress.hide";
    public static final String PROGRESS_BAR_UPDATE = "progress.update";
    public static final String PLAY_PAUSE_ACTION = "com.kharbanrama.audio.player.play.pause";
    public static final String NEXT_AUDIO_NAME = "com.kharbanrama.audio.player.next";
    public static final String PREVIOUS_AUDIO_NAME = "com.kharbanrama.audio.player.previous";
    public static final String NEXT_AUDIO_INDEX = "com.kharbanrama.audio.player.audio.index";
}