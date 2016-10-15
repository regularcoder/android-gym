package io.github.regularcoder.shakeunlock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static class AdminRec extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
                abortBroadcast();
            }
            super.onReceive(context, intent);
        }
        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager mSensorManager;
        Sensor mSensor;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        TextView message = (TextView) findViewById(R.id.message);
        message.setText("Alarm alarm");

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    float last_x, last_y, last_z;
    long lastSensorTime;

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x, y, z;

        //Retrieve latest values
        long sensorTime = System.currentTimeMillis();

        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        long diffTime = sensorTime - lastSensorTime;

        if(diffTime > 100) {
            //Detect shaking
            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > 800) {

                TextView message = (TextView) findViewById(R.id.message);
                message.setText(String.valueOf("shake detected t/ speed: " + speed));
            }

        //Store old values
        last_x = x;
        last_y = y;
        last_z = z;
        lastSensorTime = sensorTime;
        }
    }

    public void startUnlockService(View view) {
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
