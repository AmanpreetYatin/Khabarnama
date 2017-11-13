// Generated code from Butter Knife. Do not modify!
package com.bxs.khabarnama.ui.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.bxs.khabarnama.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AllStationCategoryFragment_ViewBinding implements Unbinder {
  private AllStationCategoryFragment target;

  private View view2131624130;

  @UiThread
  public AllStationCategoryFragment_ViewBinding(final AllStationCategoryFragment target,
      View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tv_name_letter1, "field 'mTvNameLetter1' and method 'click1'");
    target.mTvNameLetter1 = Utils.castView(view, R.id.tv_name_letter1, "field 'mTvNameLetter1'", TextView.class);
    view2131624130 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.click1();
      }
    });
    target.mTvArtistName1 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name1, "field 'mTvArtistName1'", TextView.class);
    target.mTvNameLetter2 = Utils.findRequiredViewAsType(source, R.id.tv_name_letter2, "field 'mTvNameLetter2'", TextView.class);
    target.mTvArtistName2 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name2, "field 'mTvArtistName2'", TextView.class);
    target.mTvNameLetter3 = Utils.findRequiredViewAsType(source, R.id.tv_name_letter3, "field 'mTvNameLetter3'", TextView.class);
    target.mTvArtistName3 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name3, "field 'mTvArtistName3'", TextView.class);
    target.mTvNameLetter4 = Utils.findRequiredViewAsType(source, R.id.tv_name_letter4, "field 'mTvNameLetter4'", TextView.class);
    target.mTvArtistName4 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name4, "field 'mTvArtistName4'", TextView.class);
    target.mTvNameLetter5 = Utils.findRequiredViewAsType(source, R.id.tv_name_letter5, "field 'mTvNameLetter5'", TextView.class);
    target.mTvArtistName5 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name5, "field 'mTvArtistName5'", TextView.class);
    target.mTvNameLetter6 = Utils.findRequiredViewAsType(source, R.id.tv_name_letter6, "field 'mTvNameLetter6'", TextView.class);
    target.mTvArtistName6 = Utils.findRequiredViewAsType(source, R.id.tv_artist_name6, "field 'mTvArtistName6'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AllStationCategoryFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvNameLetter1 = null;
    target.mTvArtistName1 = null;
    target.mTvNameLetter2 = null;
    target.mTvArtistName2 = null;
    target.mTvNameLetter3 = null;
    target.mTvArtistName3 = null;
    target.mTvNameLetter4 = null;
    target.mTvArtistName4 = null;
    target.mTvNameLetter5 = null;
    target.mTvArtistName5 = null;
    target.mTvNameLetter6 = null;
    target.mTvArtistName6 = null;

    view2131624130.setOnClickListener(null);
    view2131624130 = null;
  }
}
