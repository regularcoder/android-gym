package io.github.regularcoder.shakeunlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startUnlockService();

        setupSensitivityControl();

        setupServiceToggle();
    }

    int progressChanged = 0;

    private Context ctx = this;

    private  void setupSensitivityControl() {
        SeekBar sensitivityControl = (SeekBar) findViewById(R.id.sensitivityControl);

        sensitivityControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(Constants.ACTION.SENSITIVITY_CHANGED_ACTION);

                intent.putExtra("sensitivity", progressChanged);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
            }
        });
    }

    private void setupServiceToggle() {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.serviceToggle);

        //Set toggle button status based on current status
        toggle.setChecked(isMyServiceRunning(UnlockService.class));

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Start the service
                    startUnlockService();
                } else {
                    //Stop the service
                    Intent intent = new Intent(Constants.ACTION.STOP_SERVICE_MAIN_ACTION);
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                }
            }
        });
    }

    //http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startUnlockService() {
        Intent intent = new Intent(this, UnlockService.class);
        startService(intent);
    }

    public void lockScreen(View view) {
        DevicePolicyManager mgr = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, AdminRec.class);

        if(!mgr.isAdminActive(mAdminName)) {
            Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "needs access to lock your screen");
            intent1.putExtra("force-locked", DeviceAdminInfo.USES_POLICY_FORCE_LOCK);

            startActivity(intent1);
        }
        else {
            mgr.lockNow();
        }
    }
}
