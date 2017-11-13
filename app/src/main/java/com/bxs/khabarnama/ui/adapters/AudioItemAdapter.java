package com.bxs.khabarnama.ui.adapters;


import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.Constants;
import com.bxs.khabarnama.ui.Utils.Utilities;
import com.bxs.khabarnama.ui.models.AudioModel;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import static com.bxs.khabarnama.ui.Utils.Constants.DOWNLOADING_MESSAGE;
import static com.bxs.khabarnama.ui.Utils.Constants.NO_INTERNET_MESSAGE;


public class AudioItemAdapter extends RecyclerView.Adapter<AudioItemAdapter.ViewHolder> {
    private OnItemClickListener mOnItemClickListener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(AudioModel item, int positon);
    }

    public Context mContext;
    private ArrayList<AudioModel> audioModelArrayList;
    private File storageDir;
    Dialog dialog;
    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;


    private static String file_url = "";
    private NotificationManager mNotifyManager;
    private android.support.v7.app.NotificationCompat.Builder mBuilder;


    //
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public AudioItemAdapter(FragmentActivity activity, ArrayList<AudioModel> songsList) {
        this.mContext = activity;
        this.audioModelArrayList = songsList;
        mPref = this.mContext.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.apply();


    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMonth;
        private TextView tvDate;
        private TextView tvTitle;
        private TextView tvDesc;
        private TextView tvTime;
        private ImageView btnDownload;
        private Typeface fontTitle;
        private Typeface robotoBold;
        private Typeface fontDesc;
        private CardView cardView;


        private ViewHolder(View view) {
            super(view);
            tvMonth = (TextView) view.findViewById(R.id.tvMonth);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            cardView = (CardView) view.findViewById(R.id.cardView);
            btnDownload = (ImageView) view.findViewById(R.id.btn_download);
            fontTitle = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Medium.ttf");
            fontDesc = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
            robotoBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Bold.ttf");
            tvDesc.setTypeface(fontTitle);
            tvTime.setTypeface(fontTitle);
            tvTitle.setTypeface(fontDesc);
//            disableTouchTheft(btnDownload);
            downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    boolean result = Utilities.checkPermission(mContext);
                    if (Utilities.isConnectingToInternet(mContext)) {
                        if (result) {
                            Toast.makeText(mContext, DOWNLOADING_MESSAGE, Toast.LENGTH_SHORT).show();
                            file_url = audioModelArrayList.get(getAdapterPosition()).getUrlName();

                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                downloadFilebeforeKitKat(file_url);
                            } else {
                                downloadfileAboveKitKat(file_url);
                            }
                        }
                    } else {
                        Utilities.toast(mContext, NO_INTERNET_MESSAGE);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(audioModelArrayList.get(getLayoutPosition()), getLayoutPosition());
                }
            });

        }


    }


    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public AudioItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio, parent, false);

        return new AudioItemAdapter.ViewHolder(itemView);

    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @Override
    public void onBindViewHolder(AudioItemAdapter.ViewHolder holder, final int position) {
        holder.tvTitle.setTypeface(holder.fontTitle);
        holder.tvMonth.setTypeface(holder.robotoBold);
        holder.tvDate.setTypeface(holder.robotoBold);

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#d5d5d5"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        if (audioModelArrayList.get(position).getSongTitle().equalsIgnoreCase("")) {
            holder.tvTitle.setText("");
        } else {
            holder.tvTitle.setText(audioModelArrayList.get(position).getStRadioStName());
        }

        holder.tvMonth.setText(audioModelArrayList.get(position).getMnth());
        holder.tvDesc.setText(audioModelArrayList.get(position).getSongTitle());
        holder.tvTime.setText(audioModelArrayList.get(position).getfDur());
        holder.tvDate.setText(audioModelArrayList.get(position).getDay());

        storageDir = new File(Environment.getExternalStorageDirectory() + "/khabar1");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

    }

    @Override
    public int getItemCount() {
        return audioModelArrayList.size();
    }

    private void downloadfileAboveKitKat(String file_url) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/khabarnamafiles");

        if (!direct.exists()) {
            direct.mkdirs();
        }


        String filename = FilenameUtils.getBaseName(file_url);
        String fileExtension = FilenameUtils.getExtension(file_url);
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file_url));
        request.setTitle(filename + "." + fileExtension);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename + "." + fileExtension);
        manager.enqueue(request);


    }

    private void downloadFilebeforeKitKat(final String file_url) {
        String filename = FilenameUtils.getBaseName(file_url);
        final Uri destinationUri = Uri.parse(mContext.getExternalCacheDir() + filename);
        final Uri destinationUriw = Uri.parse(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + filename);
        DownloadRequest downloadRequest = new DownloadRequest(Uri.parse(file_url));
        downloadRequest.addCustomHeader("Auth-Token", "AIzaSyCRupwHrNzD5lWxZZdg-Cb8As-KDdUmIWk");
        downloadRequest.setRetryPolicy(new DefaultRetryPolicy());
        downloadRequest.setDestinationURI(destinationUriw);
        downloadRequest.setPriority(DownloadRequest.Priority.HIGH);
        downloadRequest.setDownloadListener(new DownloadStatusListener() {


            @Override
            public void onDownloadComplete(int id) {
                Toast.makeText(mContext, "download complete", Toast.LENGTH_SHORT).show();
                dialog.hide();
                mBuilder.setContentText("Download complete");
                // Removes the progress bar
                mBuilder.setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());

            }

            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                Toast.makeText(mContext, "download failed", Toast.LENGTH_SHORT).show();
                // pb.setVisibility(View.GONE);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {
                mBuilder.setProgress(100, progress, false);
                mNotifyManager.notify(id, mBuilder.build());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File file = new File(String.valueOf(destinationUriw)); // set your audio path
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
                mBuilder.setContentIntent(pIntent).build();

            }


        });
        downloadManager.add(downloadRequest);
    }

}
