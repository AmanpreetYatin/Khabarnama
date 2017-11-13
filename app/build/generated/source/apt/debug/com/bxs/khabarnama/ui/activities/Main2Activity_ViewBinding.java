// Generated code from Butter Knife. Do not modify!
package com.bxs.khabarnama.ui.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.bxs.khabarnama.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Main2Activity_ViewBinding implements Unbinder {
  private Main2Activity target;

  @UiThread
  public Main2Activity_ViewBinding(Main2Activity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Main2Activity_ViewBinding(Main2Activity target, View source) {
    this.target = target;

    target.mToolBar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolBar'", Toolbar.class);
    target.mFabButton = Utils.findRequiredViewAsType(source, R.id.fab, "field 'mFabButton'", FloatingActionButton.class);
    target.mDrawerLayout = Utils.findRequiredViewAsType(source, R.id.drawer_layout, "field 'mDrawerLayout'", DrawerLayout.class);
    target.mNavigationView = Utils.findRequiredViewAsType(source, R.id.nav_view, "field 'mNavigationView'", NavigationView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    Main2Activity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolBar = null;
    target.mFabButton = null;
    target.mDrawerLayout = null;
    target.mNavigationView = null;
  }
}
