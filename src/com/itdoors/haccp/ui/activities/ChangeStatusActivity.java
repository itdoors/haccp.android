
package com.itdoors.haccp.ui.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.itdoors.haccp.Intents;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.PointStatus;
import com.itdoors.haccp.rest.AsyncSQLiteTransactionalOperations;
import com.itdoors.haccp.ui.fragments.ChangeStatusFragment;
import com.itdoors.haccp.utils.ToastUtil;

public class ChangeStatusActivity extends AddDataActivity {

    @Override
    protected Fragment getNewAddDataFragment() {
        return new ChangeStatusFragment();
    }

    @Override
    protected int getFragmentResourceId() {
        return R.id.change_status_frame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);
        setTitle(R.string.add_data_record);
    }

    public static class ChangeStatusEvent {

        private final PointStatus pointStatus;

        public ChangeStatusEvent(PointStatus pointStatus) {
            this.pointStatus = pointStatus;
        }

        public PointStatus getPointStatus() {
            return pointStatus;
        }
    }

    public void onEventMainThread(ChangeStatusEvent event) {
        PointStatus status = event.getPointStatus();
        String pointId = getIntent().getStringExtra(Intents.Point.UID);
        if (pointId == null)
            return;
        Location currentLocation = getLocationClient().getLastLocation();
        AsyncSQLiteTransactionalOperations.startUpdatePointStatus(getContentResolver(), pointId, status.getId(),
                currentLocation);
        ToastUtil.ToastLong(getApplicationContext(),
                getString(R.string.data_will_be_entered_on_the_server));
        finish();
    }

}
