
package com.itdoors.haccp.ui.activities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.GroupCharacteristic;
import com.itdoors.haccp.rest.AsyncSQLiteTransactionalOperations;
import com.itdoors.haccp.ui.fragments.AddStatisticsFragment;
import com.itdoors.haccp.utils.ToastUtil;

public class AddStatisticsActivity extends AddDataActivity {

    @Override
    protected Fragment getNewAddDataFragment() {
        return new AddStatisticsFragment();
    }

    @Override
    protected int getFragmentResourceId() {
        return R.id.add_statictics_frame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_statictics);
        setTitle(R.string.add_data_record);

    }

    public static class AddStatisticsUserEvent {

        private final HashMap<GroupCharacteristic, Double> values;

        public AddStatisticsUserEvent(HashMap<GroupCharacteristic, Double> values) {
            this.values = new HashMap<GroupCharacteristic, Double>();
            this.values.putAll(values);
        }

        public HashMap<GroupCharacteristic, Double> getValues() {
            return values;
        }
    }

    public void onEventMainThread(AddStatisticsUserEvent event) {

        HashMap<GroupCharacteristic, Double> values = event.getValues();
        String pointId = getIntent().getStringExtra(Intents.Point.UID);
        if (pointId == null)
            return;

        Iterator<Entry<GroupCharacteristic, Double>> iterator = values.entrySet().iterator();
        Entry<GroupCharacteristic, Double> entry = null;

        if (iterator.hasNext())
            entry = iterator.next();
        if (entry == null)
            return;

        GroupCharacteristic characteristic = entry.getKey();
        Double value = entry.getValue();
        String date = Long.toString(Calendar.getInstance().getTime().getTime() / 1000);
        // Get the current location
        Location currentLocation = getLocationClient().getLastLocation();

        AsyncSQLiteTransactionalOperations.startInsertStatistics(getContentResolver(), pointId,
                characteristic.getId(), date, date, Integer.toString(value.intValue()),
                currentLocation);
        ToastUtil.ToastLong(getApplicationContext(),
                getString(R.string.data_will_be_entered_on_the_server));
        finish();
    }

}
