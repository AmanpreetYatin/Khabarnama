// Generated code from Butter Knife. Do not modify!
package com.bxs.khabarnama.ui.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.bxs.khabarnama.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PanuFragment_ViewBinding implements Unbinder {
  private PanuFragment target;

  @UiThread
  public PanuFragment_ViewBinding(PanuFragment target, View source) {
    this.target = target;

    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.recycleview, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PanuFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecyclerView = null;
  }
}
