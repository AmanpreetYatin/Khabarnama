package com.bxs.khabarnama.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.Typefaces;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;



@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SplashActivity extends Activity {
    private Intent intent = null;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ShimmerTextView tv;
    private Shimmer shimmer;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        verifyStoragePermissions(SplashActivity.this);

        Window w = getWindow(); // in Activity's onCreate() for instance
        //  w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //TitanicTextView tv = (TitanicTextView) findViewById(R.id.my_text_view);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        // set fancy typeface
        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        // start animation
         toggleAnimation(tv);

    }
    public void toggleAnimation(View target) {
            shimmer = new Shimmer();
            shimmer.start(tv);
            shimmer.setRepeatCount(4)
                    .setDuration(300)
                    .setStartDelay(200)
                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR)
                    .setAnimatorListener(new Animator.AnimatorListener(){
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            intent =  new Intent(SplashActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}

