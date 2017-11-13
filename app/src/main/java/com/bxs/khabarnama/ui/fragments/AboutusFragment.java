package com.bxs.khabarnama.ui.fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bxs.khabarnama.R;
import com.bxs.khabarnama.ui.Utils.Constants;

import java.util.List;

/**
 * Created by aman on 16/9/16.
 */

public class AboutusFragment extends Fragment  {
    private FrameLayout flFacebook;
    private FrameLayout flYoutube;
    private FrameLayout flGmail;
    private FrameLayout llWebsite;
   // private String pageId =  "901365739928892";
    private String pageId1 = "531258360404074";
    private String userName = "Amanpreet55";
    private Typeface fontTitle;
    private Typeface robotoRegular;
    private TextView tvContact;
    private TextView tvContactDesc;
    private TextView tvFb;
    private TextView tvFbDesc;
    private TextView tvYoutube;
    private TextView tvYoutubeDesc;
    private TextView tvWebsite;
    private TextView tvWebsiteDesc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        init(rootView);
        flFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFacebook(getActivity());

            }
        });
        flYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchYoutube(getActivity());

            }
        });
        flGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  launchGmail(getActivity());
                Toast.makeText(getActivity(),"In Progress",Toast.LENGTH_SHORT).show();

            }
        });
        llWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchWebsite(getActivity());

            }
        });

        return rootView;
    }
    public final void  launchWebsite(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        final String urlBrowser = "http://radiokhabarnama.com";
        intent.setData(Uri.parse(urlBrowser));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }
    public final void launchFacebook(Context context) {
        final String urlFb = "fb://page/"+pageId1;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/"+pageId1;
            intent.setData(Uri.parse(urlBrowser));
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }
    public final void launchYoutube(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        final String urlBrowser = "https://www.youtube.com/c/radiokhabarnama";
        intent.setData(Uri.parse(urlBrowser));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

    }
    public final void launchGmail(Context context) {

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "jaideepthind@gmail.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Radio Khabarnama");
            final PackageManager pm = getActivity().getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") ||
                        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
            if (best != null)
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            try {
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            } catch (ActivityNotFoundException e) {
                //TODO: Handle case where no email app is available
            }

    }
    private void init(View rootView){
        flFacebook = (FrameLayout) rootView.findViewById(R.id.frame_facebook);
        flYoutube = (FrameLayout) rootView.findViewById(R.id.frame_youtube);
        flGmail = (FrameLayout) rootView.findViewById(R.id.frame_gmail);
        fontTitle = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
        robotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        //
        tvContact = (TextView) rootView.findViewById(R.id.tv_contact_us);
        tvContactDesc = (TextView) rootView.findViewById(R.id.tv_contact_desc);
        tvFb = (TextView) rootView.findViewById(R.id.tv_fb);
        tvYoutube = (TextView) rootView.findViewById(R.id.tv_youtube);
        tvYoutubeDesc = (TextView) rootView.findViewById(R.id.tv_youtube_desc);
        tvWebsite = (TextView) rootView.findViewById(R.id.tv_website);
        tvWebsiteDesc = (TextView) rootView.findViewById(R.id.tv_website_desc);
        tvFbDesc = (TextView) rootView.findViewById(R.id.tv_fb_desc);
        llWebsite = (FrameLayout) rootView.findViewById(R.id.llwebsite);
        tvContact.setTypeface(fontTitle);
        tvYoutube.setTypeface(fontTitle);
        tvFb.setTypeface(fontTitle);

        tvContactDesc.setTypeface(robotoRegular);
        tvFbDesc.setTypeface(robotoRegular);
        tvYoutubeDesc.setTypeface(robotoRegular);

    }



}
