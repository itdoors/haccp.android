
package com.itdoors.haccp.ui.activities;

import java.util.Arrays;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.client.android.CaptureActivity;
import com.itdoors.haccp.R;
import com.itdoors.haccp.parser.ScanResultParser;
import com.itdoors.haccp.provider.HaccpContract;
import com.itdoors.haccp.provider.RestContentProvider;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.ui.fragments.AboutFragment;
import com.itdoors.haccp.ui.fragments.CompaniesFragment;
import com.itdoors.haccp.ui.fragments.ProfileFragment;
import com.itdoors.haccp.ui.fragments.QRFragment;
import com.itdoors.haccp.ui.interfaces.SetQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRCallback;
import com.itdoors.haccp.ui.interfaces.TakeQRListener;
import com.itdoors.haccp.utils.AppUtils;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;

public class HomeActivity extends SherlockFragmentActivity implements
        SetQRCallback,
        TakeQRListener,
        CompaniesFragment.OnCompanyPressedListener,
        ProfileFragment.OnLogoutPressedListener
{

    private static final String SELECTED = "selected";

    private static final int MAX_DURATION_FOR_DRAWER_OPEN_CLOSE = 600; // in mls
    public static final int GET_RECOGNIZED_TEXT_REQUEST_CODE = 111;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mTitles;

    private BaseAdapter mAdapter;
    private int mSelectedItem;

    private TakeQRCallback mTakeCodeFromCameraCallback;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (!SyncUtils.isLoggedIn(this, getIntent())) {
            Logger.Logi(getClass(), "Need to login first...");
            finish();
            return;
        }

        if (!SyncUtils.cheakSync(this, getIntent())) {
            Logger.Logi(getClass(), "Need sync first...");
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        mTitle = mDrawerTitle = getTitle();
        mTitles = getResources().getStringArray(R.array.menu_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setCacheColorHint(Color.TRANSPARENT);
        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        mSelectedItem = (savedInstanceState == null) ? 1 : savedInstanceState.getInt(SELECTED);

        mDrawerList.setAdapter(mAdapter = buildAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /*
                                       * "open drawer" description for
                                       * accessibility
                                       */
                R.string.drawer_close /*
                                       * "close drawer" description for
                                       * accessibility
                                       */
                ) {
                    public void onDrawerClosed(View view) {
                        getSupportActionBar().setTitle(mTitle);
                        supportInvalidateOptionsMenu(); // creates call to
                                                        // onPrepareOptionsMenu()
                    }

                    public void onDrawerOpened(View drawerView) {
                        getSupportActionBar().setTitle(mDrawerTitle);
                        supportInvalidateOptionsMenu(); // creates call to
                                                        // onPrepareOptionsMenu()
                    }
                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null)
            selectItem(mSelectedItem);

    }

    private BaseAdapter buildAdapter() {

        TypedArray imgs = getResources().obtainTypedArray(R.array.menu_items_img);
        TypedArray titles = getResources().obtainTypedArray(R.array.menu_items);

        Menu[] menus = new Menu[mTitles.length];
        int len = titles.length();

        for (int index = 0; index < len; index++) {
            Menu menu = new Menu();
            menu.imgRes = imgs.getResourceId(index, 0);
            menu.strRes = titles.getResourceId(index, 0);
            menus[index] = menu;
        }
        Logger.Logi(getClass(), Arrays.toString(menus));

        imgs.recycle();
        titles.recycle();

        return new MenuAdapter(this, R.layout.drawer_list_item, menus);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED, mSelectedItem);
    }

    public int getSelectedViewPosition() {
        return mSelectedItem;
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (position == 3) {// Settings

                final int previouslySelectedItem = mSelectedItem;
                mSelectedItem = position;
                mAdapter.notifyDataSetChanged();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSelectedItem = previouslySelectedItem;
                    }
                }, 100);
            }
            else {
                mSelectedItem = position;
                mAdapter.notifyDataSetChanged();
            }

            // view.getFocusables(position);
            // view.setSelected(true);
            selectItem(position);
        }
    }

    private void replaceFragment(final Fragment fragment) {

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }, MAX_DURATION_FOR_DRAWER_OPEN_CLOSE);

    }

    private void selectItem(int position) {

        Fragment frg = null;

        switch (position) {
            case 0:
                frg = new ProfileFragment();
                break;
            case 1:
                frg = new QRFragment();
                break;
            case 2:
                onPointsPressed();
                break;
            case 3:
                onSettingsPressed();
                break;
            case 4:
                frg = new AboutFragment();
                break;
        }
        if (frg != null) {
            replaceFragment(frg);
            setTitle(mTitles[position]);
        }
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (onDrawerToggleOnptionItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_RECOGNIZED_TEXT_REQUEST_CODE) {
                mTakeCodeFromCameraCallback.codeFromCameraCallback(data);
                try {

                    Logger.Logd(getClass(), "data:" + data);
                    String id = (String) (new ScanResultParser()).parse(data
                            .getStringExtra("SCAN_RESULT"));
                    Intent intent = PointDetailsActivity.newIntent(this, id);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.ToastLong(getApplicationContext(), getString(R.string.scanner_error));
                }
            }
        }
    }

    private boolean onDrawerToggleOnptionItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home
                && mDrawerToggle.isDrawerIndicatorEnabled()) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public void takeQR() {

        if (Enviroment.checkCameraHardware(this)) {

            Intent intent = new Intent(this, CaptureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setAction("com.itdoors.haccp.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            intent.putExtra("SAVE_HISTORY", false);
            startActivityForResult(intent, GET_RECOGNIZED_TEXT_REQUEST_CODE);

        } else {
            ToastUtil.ToastLong(getApplicationContext(),
                    getResources().getString(R.string.your_device_has_no_camera));
        }
    }

    @Override
    public void setTakeCodeFromCameraCallBack(TakeQRCallback callback) {
        this.mTakeCodeFromCameraCallback = callback;
    }

    private static class MenuAdapter extends ArrayAdapter<Menu> {

        private LayoutInflater mLayoutInflater;
        private int mResource;

        public MenuAdapter(Context context, int resource, Menu[] items) {
            super(context, resource, items);
            mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(mResource, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_list_name);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Menu item = getItem(position);
            viewHolder.textView.setText(item.strRes);
            AppUtils.setScaledCompoundDrawable(getContext(), item.imgRes, viewHolder.textView);

            if (position == ((HomeActivity) getContext()).getSelectedViewPosition())
                convertView.setBackgroundColor(Color.RED);
            else
                convertView.setBackgroundColor(Color.TRANSPARENT);

            return convertView;

        }

        private static class ViewHolder {
            TextView textView;
        }
    }

    private static class Menu {
        int imgRes, strRes;
    }

    public void onSettingsPressed() {
        handler.postDelayed(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }, MAX_DURATION_FOR_DRAWER_OPEN_CLOSE);
    }

    public void onPointsPressed() {
        CompaniesFragment fragment = new CompaniesFragment();
        replaceFragment(fragment);
        setTitle(R.string.companies);
    }

    @Override
    public void onCompanyPressedListener(com.itdoors.haccp.model.Company companyObject) {
        startActivity(CompanyObjectsActivity.newIntentInstance(this, companyObject));
    };

    @Override
    public void onLogoutPressed() {
        getContentResolver().delete(RestContentProvider.BASE_CONTENT_URI, null, null);
        // delete db
        SyncUtils.logOut(getApplicationContext(), getIntent());
        finish();
    }

    public interface ComapiesQuery {
        String[] PROJECTION = {
                HaccpContract.Companies._ID,
                HaccpContract.Companies.NAME,
                HaccpContract.Companies.UID,
        };
        int _ID = 0;
        int NAME = 1;
        int UID = 2;
    }

}
