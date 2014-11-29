
package com.itdoors.haccp.ui.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.itdoors.haccp.Global;
import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.itdoors.haccp.rest.robospice_retrofit.GetStatisticsRequest;
import com.itdoors.haccp.rest.robospice_retrofit.MySpiceService;
import com.itdoors.haccp.sync.SyncUtils;
import com.itdoors.haccp.ui.fragments.AttributesFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOfflineFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment;
import com.itdoors.haccp.ui.fragments.StatisticsOnlineFragment.MODE;
import com.itdoors.haccp.ui.fragments.SwipeRefreshListFragment;
import com.itdoors.haccp.ui.fragments.TimeRangeDialogFragment;
import com.itdoors.haccp.ui.interfaces.OnContextMenuItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnLongStatisticsItemPressedListener;
import com.itdoors.haccp.ui.interfaces.OnTimeRangeChooseListener;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.Enviroment;
import com.itdoors.haccp.utils.Logger;
import com.itdoors.haccp.utils.ToastUtil;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

public class PointDetailsActivity extends SherlockFragmentActivity implements

        ViewPager.OnPageChangeListener,
        OnContextMenuItemPressedListener,
        OnLongStatisticsItemPressedListener,
        StatisticsOnlineFragment.TimeRangeParametersHolder,
        StatisticsOnlineFragment.StatisticsListModeHolder,
        OnTimeRangeChooseListener,
        OnRefreshListener,
        TabListener,
        OnNavigationListener
{

    private static final String CHOOSE_TIME_RANGE_TYPE_DIALOG = "com.itdoors.haccp.activities.PointDetailsActivity.CHOOSE_TIME_RANGE_TYPE_DIALOG";
    private static final String STATISTICS_MODE_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_MODE_SAVE_KEY";
    private static final String STATISTICS_FROM_TIME_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_FROM_TIME_SAVE_KEY";
    private static final String STATISTICS_TO_TIME_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.STATISTICS_TO_TIME_SAVE_KEY";
    private static final String TITLE_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.TITLE_SAVE_KEY";
    private static final String NETWORK_MODE_SAVE_KEY = "com.itdoors.haccp.activities.PointDetailsActivity.NETWORK_MODE_SAVE_KEY";

    private static final int TIME_RANGE_REQUEST = 0x0abc;

    private static String ONLINE = "online";
    private static String OFFLINE = "offline";
    private static String ATTRIBUTES = "attributes";

    protected static enum Mode {
        ONLINE, OFFLINE;
    }

    public static Intent newIntent(Activity activity, String id) {
        Intent intent = new Intent(activity, PointDetailsActivity.class);
        intent.putExtra(Intents.Point.UID, id);
        return intent;
    }

    private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);

    private ArrayPagerAdapter<Fragment> mViewPagerAdapter;
    private ViewPager mViewPager;

    // private Fragment mStatisticsFragment;
    private StatisticsOnlineFragment.MODE mStatisticFragmentMode;
    private String fromTimeStamp;
    private String toTimeStamp;

    @SuppressWarnings("unused")
    private ActionMode mActionMode;

    private Mode networkMode;
    private Handler handler = new Handler();

    RequestListener<MoreStatistics> mStatisticsRefreshRequestListener = new RequestListener<MoreStatistics>() {
        @Override
        public void onRequestFailure(SpiceException exception) {
            ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
            hideSwipeAnimation();
        }

        @Override
        public void onRequestSuccess(MoreStatistics statistics) {

            updateStatisticsAfterRefreshSuccess(statistics);
            PointDetailsActivity.this.mStatisticFragmentMode = MODE.GENERAL;
            hideSwipeAnimation();
        }
    };

    PendingRequestListener<MoreStatistics> mRefreshPendingRequestListener = new PendingRequestListener<MoreStatistics>() {

        @Override
        public void onRequestFailure(SpiceException exception) {
            ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
            hideSwipeAnimation();
        }

        @Override
        public void onRequestSuccess(MoreStatistics statistics) {
            updateStatisticsAfterRefreshSuccess(statistics);
            PointDetailsActivity.this.mStatisticFragmentMode = MODE.GENERAL;
            hideSwipeAnimation();
        }

        @Override
        public void onRequestNotFound() {
        }
    };

    RequestListener<MoreStatistics> mStatisticsFromTimeRangeRequestListener = new RequestListener<MoreStatistics>() {
        @Override
        public void onRequestFailure(SpiceException exception) {
            ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
            hideSwipeAnimation();
        }

        @Override
        public void onRequestSuccess(MoreStatistics statistics) {

            PointDetailsActivity.this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
            updateStatisticsAfterTimeRangeLoadSuccess(statistics);
            hideSwipeAnimation();
        }
    };

    PendingRequestListener<MoreStatistics> mFromTimeRangePendingRequestListener = new PendingRequestListener<MoreStatistics>() {

        @Override
        public void onRequestFailure(SpiceException exception) {
            ToastUtil.ToastLong(getApplicationContext(), getString(R.string.failed_to_load_data));
            hideSwipeAnimation();
        }

        @Override
        public void onRequestSuccess(MoreStatistics statistics) {
            PointDetailsActivity.this.mStatisticFragmentMode = MODE.FROM_TIME_RANGE;
            updateStatisticsAfterTimeRangeLoadSuccess(statistics);
            hideSwipeAnimation();
        }

        @Override
        public void onRequestNotFound() {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        Logger.Loge(getClass(), "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null) {

            String id = getIntent().getExtras().getString(Intents.Point.UID);
            String token = SyncUtils.getAccessToken(getApplicationContext());

            if (mStatisticFragmentMode == MODE.GENERAL)
                spiceManager
                        .addListenerIfPending(MoreStatistics.class,
                                GetStatisticsRequest.getCacheKey(token, id),
                                mRefreshPendingRequestListener);
            if (fromTimeStamp != null && toTimeStamp != null
                    && mStatisticFragmentMode == MODE.FROM_TIME_RANGE)
                spiceManager.addListenerIfPending(MoreStatistics.class,
                        GetStatisticsRequest.getCacheKey(token, id, fromTimeStamp, toTimeStamp),
                        mFromTimeRangePendingRequestListener);
        }
        Logger.Loge(getClass(), "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        Logger.Loge(getClass(), "onPause()");
    }

    @Override
    protected void onStop() {
        if (spiceManager.isStarted())
            spiceManager.shouldStop();
        super.onStop();

        Logger.Loge(getClass(), "onStop()");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Logger.Loge(getClass(), "onDestroy()");
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        mStatisticFragmentMode = MODE.GENERAL;
        String title = getResources().getString(R.string.control_point);
        boolean isNetworkAlive = Enviroment.isNetworkAvaliable(this);
        networkMode = isNetworkAlive ? Mode.ONLINE : Mode.OFFLINE;

        if (savedInstanceState != null) {

            networkMode = (Mode) savedInstanceState.getSerializable(NETWORK_MODE_SAVE_KEY);
            mStatisticFragmentMode = (MODE) savedInstanceState
                    .getSerializable(STATISTICS_MODE_SAVE_KEY);
            fromTimeStamp = savedInstanceState.getString(STATISTICS_FROM_TIME_SAVE_KEY);
            toTimeStamp = savedInstanceState.getString(STATISTICS_TO_TIME_SAVE_KEY);
            title = savedInstanceState.getString(TITLE_SAVE_KEY);

        }

        setTitle(title);
        setContentView(R.layout.activity_control_point);

        mViewPager = (ViewPager) findViewById(R.id.cp_pager);

        if (mViewPager != null) {

            mViewPagerAdapter = buildViewPagerAdapter();

            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.setOnPageChangeListener(this);
            mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
            mViewPager.setPageMargin(getResources()
                    .getDimensionPixelSize(R.dimen.page_margin_width));

            buildActionBar();

        }

        Logger.Loge(getClass(), "onCreate()");
    }

    private void buildActionBar() {

        final ActionBar actionBar = getSupportActionBar();
        int navigationMode = ActionBar.NAVIGATION_MODE_STANDARD;

        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = Enviroment.isTablet(getApplicationContext());

        if (isTablet) {
            navigationMode = ActionBar.NAVIGATION_MODE_TABS;
        }
        else {
            navigationMode = (orientation == Configuration.ORIENTATION_PORTRAIT)
                    ? ActionBar.NAVIGATION_MODE_TABS
                    : ActionBar.NAVIGATION_MODE_LIST;
        }

        actionBar.setNavigationMode(navigationMode);

        if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.statistics)
                    .setTabListener(this));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.attributes)
                    .setTabListener(this));
        }
        else if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
            Context context = getSupportActionBar().getThemedContext();
            ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context,
                    R.array.point_details_navigation, R.layout.sherlock_spinner_item);
            list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(list, this);
        }

        View actionPanel = null;
        View.OnClickListener mOnActionPanelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomPanelPressed(v);
            }
        };

        if (isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {

            actionPanel = LayoutInflater.from(this).inflate(R.layout.top_panel_point_details, null);
            actionBar.setCustomView(actionPanel);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        else {
            actionPanel = (ViewGroup) findViewById(R.id.cp_bottom_panel);
        }
        if (actionPanel != null) {
            actionPanel.findViewById(R.id.cp_bp_add_item).setOnClickListener(
                    mOnActionPanelClickListener);
            actionPanel.findViewById(R.id.cp_bp_params_item).setOnClickListener(
                    mOnActionPanelClickListener);
            actionPanel.findViewById(R.id.cp_bp_calendar_item).setOnClickListener(
                    mOnActionPanelClickListener);
        }
    }

    private ArrayPagerAdapter<Fragment> buildViewPagerAdapter() {

        ArrayList<PageDescriptor> pages = new ArrayList<PageDescriptor>();
        String statTag = (networkMode == Mode.ONLINE) ? ONLINE : OFFLINE;
        pages.add(new SimplePageDescriptor(statTag, null));
        pages.add(new SimplePageDescriptor(ATTRIBUTES, null));
        return new SamplePagerAdapter(getSupportFragmentManager(), pages);
    }

    private static class SamplePagerAdapter extends ArrayPagerAdapter<Fragment> {

        public SamplePagerAdapter(FragmentManager fragmentManager,
                ArrayList<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected Fragment createFragment(PageDescriptor desc) {
            if (desc.getFragmentTag().equals(ONLINE))
                return new StatisticsOnlineFragment();
            if (desc.getFragmentTag().equals(OFFLINE))
                return new StatisticsOfflineFragment();
            else if (desc.getFragmentTag().equals(ATTRIBUTES))
                return new AttributesFragment();
            throw new IllegalArgumentException("Unknown fragmnent tag:" + desc.getFragmentTag());
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mViewPager.setCurrentItem(itemPosition);
        return true;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public boolean onBottomPanelPressed(View item) {

        switch (item.getId()) {

            case R.id.cp_bp_add_item:

                String pointId = getIntent().getExtras().getString(Intents.Point.UID);
                Intent intent = new Intent(this, AddStatisticsActivity.class);
                intent.putExtra(Intents.Point.UID, pointId);
                startActivity(intent);
                return true;

            case R.id.cp_bp_params_item:
                ToastUtil.ToastLong(getApplicationContext(), "Params");
                return true;

            case R.id.cp_bp_calendar_item: {
                if (Enviroment.isNetworkAvaliable(this)) {

                    TimeRangeDialogFragment dialog = new TimeRangeDialogFragment();
                    dialog.show(getSupportFragmentManager(), CHOOSE_TIME_RANGE_TYPE_DIALOG);
                    return true;

                }
                else {
                    ToastUtil.ToastLong(getApplicationContext(),
                            getString(R.string.not_avalieble_without_any_interent_connection));
                    return false;
                }
            }
            default:
                return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(NETWORK_MODE_SAVE_KEY, networkMode);
        outState.putSerializable(STATISTICS_MODE_SAVE_KEY, mStatisticFragmentMode);
        outState.putString(STATISTICS_FROM_TIME_SAVE_KEY, fromTimeStamp);
        outState.putString(STATISTICS_TO_TIME_SAVE_KEY, toTimeStamp);
        outState.putString(TITLE_SAVE_KEY, getTitle().toString());
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TIME_RANGE_REQUEST) {

                String fromTimeStamp = data
                        .getStringExtra(Intents.CalendarTimeRange.FROM_TIME_STAMP);
                String toTimeStamp = data.getStringExtra(Intents.CalendarTimeRange.TO_TIME_STAMP);

                String fromTimeStampStr = CalendarUtils.inUsualDateFromat(fromTimeStamp);
                String toTimeStampStr = CalendarUtils.inUsualDateFromat(toTimeStamp);

                ToastUtil
                        .ToastLong(getApplicationContext(), getString(R.string.from) + " : "
                                + fromTimeStampStr + ", " + getString(R.string.to) + " : "
                                + toTimeStampStr);
                onStatisticsFromTimeRangeLoad(fromTimeStamp, toTimeStamp);

            }
        }
    }

    @Override
    public MODE getMode() {
        return mStatisticFragmentMode;
    }

    @Override
    public String getFromTimeInTimeStamp() {
        return fromTimeStamp;
    }

    @Override
    public String getToTimeInTimeStamp() {
        return toTimeStamp;
    }

    private final class StatisticsActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            menu.add(getResources().getString(R.string.edit)).setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            menu.add(getResources().getString(R.string.delete)).setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            ToastUtil.ToastLong(PointDetailsActivity.this.getApplicationContext(), "Got click: "
                    + item);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

    @Override
    public void onLongStatisticsItemPressed(long itemId) {
        mActionMode = startActionMode(new StatisticsActionMode());
    }

    @Override
    public void onEditStaticticsItemContextMenuPressed(int position) {
        ToastUtil.ToastLong(getApplicationContext(), getString(R.string.edit) + ":" + position);
    }

    @Override
    public void onDeleteStaticticsItemContextMenuPressed(int position) {
        ToastUtil.ToastLong(getApplicationContext(), getString(R.string.delete) + ":" + position);
    }

    // Swipe refresh support.v4 rev.19.1;
    @Override
    public void onRefresh() {
        if (Enviroment.isNetworkAvaliable(this)) {
            if (networkMode == Mode.ONLINE) {
                refreshStatistics();
            }
            else {
                /* refreshStatistics(); */
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPagerAdapter.remove(0);
                        networkMode = Mode.ONLINE;
                        mViewPagerAdapter.insert(new SimplePageDescriptor(ONLINE, null), 0);
                        mViewPager.setCurrentItem(0);
                        hideSwipeAnimation();
                    }
                }, 1000);
            }
        }
        else {
            ToastUtil.ToastLong(getApplicationContext(),
                    getString(R.string.not_avalieble_without_any_interent_connection));
            hideSwipeAnimation();
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onTimeRagneClicked(int item_type) {

        boolean directLoad = true;

        boolean today = false;
        boolean yesterday = false;

        String fromUnixTimeStamp = null, toUnixTimeStamp = null;

        final Calendar toDateCalendar = Calendar.getInstance();
        Date toDate = CalendarUtils.getEndOfDay(toDateCalendar.getTime());

        final Calendar fromDateCalendar = Calendar.getInstance();
        Date fromDate = null;

        switch (item_type) {

            case 0:
                // Today
                fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
                today = true;
                break;
            case 1:
                // Yesterday
                fromDateCalendar.add(Calendar.DATE, -1);
                fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
                toDate = CalendarUtils.getEndOfDay(fromDate);
                yesterday = true;
                break;
            case 2:
                // LastWeak = last 7 days
                fromDateCalendar.add(Calendar.DATE, -7 + 1);
                fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
                break;
            case 3:
                // Last 30 days
                fromDateCalendar.add(Calendar.DATE, -30 + 1);
                fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
                break;
            case 4:
                // This month
                fromDateCalendar.set(Calendar.DATE, 1);
                fromDate = CalendarUtils.getStartOfDay(fromDateCalendar.getTime());
                break;
            case 5:
                directLoad = false;
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivityForResult(intent, TIME_RANGE_REQUEST);
                break;
            default:
                directLoad = false;
                break;
        }

        if (directLoad) {
            if (Enviroment.isNetworkAvaliable(this)) {

                fromUnixTimeStamp = Long.toString(fromDate.getTime() / 1000);
                toUnixTimeStamp = Long.toString(toDate.getTime() / 1000);

                String fromTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(
                        new Date(Long.valueOf(fromUnixTimeStamp) * 1000)).toString();
                String toTimeStampStr = new SimpleDateFormat(Global.usualDateFromat).format(
                        new Date(Long.valueOf(toUnixTimeStamp) * 1000)).toString();
                String toastMess = getString(R.string.from) + " : " + fromTimeStampStr + " , "
                        + getString(R.string.to) + " : " + toTimeStampStr;

                if (today)
                    toastMess = getString(R.string.today) + " : " + fromTimeStampStr;
                if (yesterday)
                    toastMess = getString(R.string.yesterday) + " : " + fromTimeStampStr;

                ToastUtil.ToastLong(getApplicationContext(), toastMess);

                onStatisticsFromTimeRangeLoad(fromUnixTimeStamp, toUnixTimeStamp);

            }
            else {
                ToastUtil.ToastLong(getApplicationContext(),
                        getString(R.string.not_avalieble_without_any_interent_connection));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    protected void showProgress() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    protected void hideProgress() {
        setSupportProgressBarIndeterminateVisibility(false);
    }

    private void refreshStatistics() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String pointId = extras.getString(Intents.Point.UID);
            GetStatisticsRequest request = new GetStatisticsRequest.Builder()
                    .setToken(SyncUtils.getAccessToken(getApplicationContext()))
                    .setId(pointId)
                    .build();
            spiceManager.execute(request, request.getCacheKey(), DurationInMillis.ONE_MINUTE,
                    mStatisticsRefreshRequestListener);
        }
    }

    private void onStatisticsFromTimeRangeLoad(String from, String to) {
        if (Enviroment.isNetworkAvaliable(getApplicationContext())) {

            this.fromTimeStamp = from;
            this.toTimeStamp = to;

            if (networkMode == Mode.ONLINE) {
                loadStatisticsFromTimeRange(from, to);
            }
            else if (networkMode == Mode.OFFLINE) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPagerAdapter.remove(0);
                        networkMode = Mode.ONLINE;
                        mViewPagerAdapter.insert(new SimplePageDescriptor(ONLINE, null), 0);
                        mViewPager.setCurrentItem(0);
                    }
                }, 1000);

            }
        }
        else {
            ToastUtil.ToastLong(getApplicationContext(),
                    getString(R.string.not_avalieble_without_any_interent_connection));
        }
    }

    private void loadStatisticsFromTimeRange(String fromUnixTimeStamp, String toUnixTimeStamp) {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String pointId = extras.getString(Intents.Point.UID);
            GetStatisticsRequest request = new GetStatisticsRequest.Builder()
                    .setToken(SyncUtils.getAccessToken(getApplicationContext()))
                    .setId(pointId)
                    .setStartDate(fromUnixTimeStamp)
                    .setEndDate(toUnixTimeStamp).build();
            spiceManager.execute(request, request.getCacheKey(), DurationInMillis.ONE_MINUTE,
                    mStatisticsFromTimeRangeRequestListener);
        }
    }

    private void updateStatisticsAfterTimeRangeLoadSuccess(MoreStatistics content) {

        Fragment fragment = mViewPagerAdapter.getExistingFragment(0);
        if (fragment != null) {
            if (networkMode == Mode.ONLINE) {
                ((StatisticsOnlineFragment) fragment).updateAfterFromTimeRangeLoad(
                        content.getStatistics(), content.getMore());
            }
        }
    }

    private void updateStatisticsAfterRefreshSuccess(MoreStatistics content) {

        Fragment fragment = mViewPagerAdapter.getExistingFragment(0);
        if (fragment != null) {
            if (networkMode == Mode.ONLINE) {
                ((StatisticsOnlineFragment) fragment).updateAfterRefresh(content.getStatistics(),
                        content.getMore());
            }
        }
    }

    private void hideSwipeAnimation() {
        Fragment fragment = mViewPagerAdapter.getExistingFragment(0);
        if (fragment != null && fragment instanceof SwipeRefreshListFragment) {
            SwipeRefreshListFragment swipeFrg = (SwipeRefreshListFragment) fragment;
            swipeFrg.setRefreshing(false);
        }
    }

}
