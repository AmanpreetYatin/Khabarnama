package com.bxs.khabarnama.ui.models;

/**
 * Created by root on 21/5/17.
 */

public class AudioModel {
    private String stRadioStName;
    private String songTitle;
    private String urlName;
    private String mnth="04";
    private String Day="Apr";
    private String fDur;
    private String fSize;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public AudioModel(String rstname, String stitle, String fldrnm, String mnt, String dys, String fd, String fs)
    {
        stRadioStName=rstname;
        songTitle=stitle;
        urlName=fldrnm;
        mnth=mnt;
        Day=dys;
        fDur=fd;
        fSize=fs;

    }
    public String getStRadioStName() {
        return stRadioStName;
    }

    public void setStRadioStName(String stRadioStName) {
        this.stRadioStName = stRadioStName;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getMnth() {
        return mnth;
    }

    public void setMnth(String mnth) {
        this.mnth = mnth;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getfDur() {
        return fDur;
    }

    public void setfDur(String fDur) {
        this.fDur = fDur;
    }

    public String getfSize() {
        return fSize;
    }

    public void setfSize(String fSize) {
        this.fSize = fSize;
    }


}
