package com.bxs.khabarnama.ui.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;


public class Utilities {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static HttpProxyCacheServer proxy;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void setupMediaPlayer(){
        if(Constants.mediaPlayer==null){
            Constants.mediaPlayer = new MediaPlayer();
            Constants.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }
    }
    public static int calculateProgress(long progress,long max){
        float a = ((float)progress/(float)max);
        float b= (a*100);
        return (int) b;
    }
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000))%60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if(hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }



    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    /////////////////////////////////////////////////////////////////////////
    /// for images
    public static ImageLoaderConfiguration config;
    public static DisplayImageOptions options;

    public static void initializeImageLoaders(Context mContext){
        config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(1)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .build();
    }

    // check for internet connection
    public static boolean isConnectingToInternet(Context mContext){
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////



    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }

    public static void execute(Context context, AsyncTask<Void, Void,Void> asyncTask){
        if(isConnectingToInternet(context)){
            asyncTask.execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    public static boolean hasData(String str){

        if(str!=null){
            if(!str.equals("")&&!str.equals("null"))
                return true;
            else
                return false;
        } else
            return false;
    }

    public static boolean hasData(HashMap hashMap){

        if(hashMap==null)
            return false;
        else {
            if(hashMap.isEmpty())
                return false;
            else
                return true;
        }
    }

    public static boolean hasData(ArrayList list){

        if(list!=null){
            if(list.size()>0)
                return true;
            else
                return false;
        } else
            return false;
    }


    public static String extractCharacters(String str, int numberOfChars){

        if(str.length()>0) {
            if(str.length()>numberOfChars)
                str = str.substring(0, numberOfChars);
        } else
            str = "";
        return str;
    }

    public static boolean cloneObjects(ArrayList sourceList, ArrayList destinationList){

        if(hasData(sourceList)){

            for(int i=0;i<sourceList.size();i++){
                destinationList.add(sourceList.get(i));
            }
            return  true;
        } else {
            return  false;
        }
    }




    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void displayImage(Context context, String imageURL, ImageView imageView){
        if(!ImageLoader.getInstance().isInited())
            initializeImageLoaders(context);

        if(hasData(imageURL)){
            ImageLoader.getInstance().displayImage(imageURL, imageView , options, null);
        }
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return true;
        }
        return false;
    }
    //load audio  from device
    /*private void loadAudio() {
        ContentResolver contentResolver = Context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        cursor.close();
    }*/


    //get month on the basis of string
    public static String getMonth(String st) {
        String st1 = st;
        switch (st) {
            case "01":
                st1 = "Jan";
                break;
            case "02":
                st1 = "Feb";
                break;
            case "03":
                st1 = "Mar";
                break;
            case "04":
                st1 = "Apr";
                break;
            case "05":
                st1 = "May";
                break;
            case "06":
                st1 = "Jun";
                break;
            case "07":
                st1 = "Jul";
                break;
            case "08":
                st1 = "Aug";
                break;
            case "09":
                st1 = "Sep";
                break;
            case "10":
                st1 = "Oct";
                break;
            case "11":
                st1 = "Nov";
                break;
            case "12":
                st1 = "Dec";
                break;

        }
        return st1;
    }
}
