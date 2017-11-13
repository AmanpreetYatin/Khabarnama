package com.bxs.khabarnama.ui.activities;import android.os.Bundle;import android.support.design.widget.FloatingActionButton;import android.support.design.widget.Snackbar;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentManager;import android.support.v4.app.FragmentTransaction;import android.view.View;import android.support.design.widget.NavigationView;import android.support.v4.view.GravityCompat;import android.support.v4.widget.DrawerLayout;import android.support.v7.app.ActionBarDrawerToggle;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.Toolbar;import android.view.Menu;import android.view.MenuItem;import android.widget.Toast;import com.bxs.khabarnama.R;import com.bxs.khabarnama.ui.fragments.AllStationCategoryFragment;import com.bxs.khabarnama.ui.fragments.HomeFragment;import com.bxs.khabarnama.ui.fragments.PanuFragment;import com.bxs.khabarnama.ui.interfaces.InterfaceCommunicator;import butterknife.BindView;import butterknife.ButterKnife;public class Main2Activity extends AppCompatActivity        implements NavigationView.OnNavigationItemSelectedListener, InterfaceCommunicator {    private static final String TAG = Main2Activity.class.getSimpleName();    @BindView(R.id.toolbar)    protected Toolbar mToolBar;    @BindView(R.id.fab)    protected FloatingActionButton mFabButton;    @BindView(R.id.drawer_layout)    protected DrawerLayout mDrawerLayout;    @BindView(R.id.nav_view)    protected NavigationView mNavigationView;    //fragments    private HomeFragment mHomeFragment;    private AllStationCategoryFragment mAllStationCategoryFragment;    private PanuFragment mPanuFragment;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_main2);        ButterKnife.bind(this);        init();        mFabButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)                        .setAction("Action", null).show();            }        });    }    private void init() {        setSupportActionBar(mToolBar);        setNavigationDrawer();    }    private void setNavigationDrawer() {        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(                this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);        mDrawerLayout.setDrawerListener(toggle);        toggle.syncState();        setFragment(getHomeFragment(), mHomeFragment.TAG, false);        mNavigationView.setCheckedItem(0);        mNavigationView.setNavigationItemSelectedListener(this);    }    @Override    public void onBackPressed() {        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {            mDrawerLayout.closeDrawer(GravityCompat.START);        } else {            super.onBackPressed();        }    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        // Inflate the menu; this adds items to the action bar if it is present.        getMenuInflater().inflate(R.menu.main2, menu);        return true;    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        // Handle action bar item clicks here. The action bar will        // automatically handle clicks on the Home/Up button, so long        // as you specify a parent activity in AndroidManifest.xml.        int id = item.getItemId();        //noinspection SimplifiableIfStatement        if (id == R.id.action_settings) {            return true;        }        return super.onOptionsItemSelected(item);    }    @SuppressWarnings("StatementWithEmptyBody")    @Override    public boolean onNavigationItemSelected(MenuItem item) {        // Handle navigation view item clicks here.        int id = item.getItemId();        boolean isSelected = false;        if (id == R.id.nav_home) {            setFragment(getHomeFragment(), mHomeFragment.TAG, false);            isSelected = true;        } else if (id == R.id.nav_Rate) {            setFragment(getAllStationCategoryFragment(), mAllStationCategoryFragment.TAG, false);            isSelected = true;        } else if (id == R.id.nav_panu) {            setFragment(getPanuFragment(), mPanuFragment.TAG, false);            isSelected = true;        }        mDrawerLayout.closeDrawer(GravityCompat.START);        return isSelected;    }    private void setFragment(Fragment fragment, String tag, boolean addToStack) {        if (fragment == null)            return;        else {            FragmentManager fragmentManager = getSupportFragmentManager();            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();            fragmentTransaction.replace(R.id.container, fragment, tag);            fragmentTransaction.addToBackStack(null);            fragmentTransaction.commit();        }    }    public HomeFragment getHomeFragment() {        mHomeFragment = HomeFragment.getInstance();        return mHomeFragment;    }    public AllStationCategoryFragment getAllStationCategoryFragment() {        mAllStationCategoryFragment = AllStationCategoryFragment.getInstance();        return mAllStationCategoryFragment;    }    public PanuFragment getPanuFragment() {        mPanuFragment = PanuFragment.newIntance();        return mPanuFragment;    }    @Override    public void getResultBackFromDialog(int position) {        Toast.makeText(getApplicationContext(), "position>" + position, Toast.LENGTH_SHORT).show();    }}