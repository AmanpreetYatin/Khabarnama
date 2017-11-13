// Generated code from Butter Knife. Do not modify!
package com.bxs.khabarnama.ui.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.bxs.khabarnama.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AllStationActivity_ViewBinding implements Unbinder {
  private AllStationActivity target;

  @UiThread
  public AllStationActivity_ViewBinding(AllStationActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AllStationActivity_ViewBinding(AllStationActivity target, View source) {
    this.target = target;

    target.mRecycleview = Utils.findRequiredViewAsType(source, R.id.recycleview, "field 'mRecycleview'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AllStationActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecycleview = null;
  }
}
