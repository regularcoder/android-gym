package io.github.regularcoder.shakeunlock;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockService extends Service implements SensorEventListener {
    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float last_x, last_y, last_z;
    long lastSensorTime;

    @Override
    public void onDestroy() {
        int x;
    }

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
                //See if screen is off
                DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
                for (Display display : dm.getDisplays()) {
                    if (display.getState() == Display.STATE_OFF) {
                        //Start activity to turn on screen
                        Intent intent = new Intent(this, HiddenUnlockActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }

            //Store old values
            last_x = x;
            last_y = y;
            last_z = z;
            lastSensorTime = sensorTime;
        }
    }

    public Handler handler = null;
    public static Runnable runnable = null;
    public Context t = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager mSensorManager;
        Sensor mSensor;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Show foreground notification so locking service will continue running
        Intent notificationIntent = new Intent(this, UnlockService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.intro_icon_1)
                .setContentTitle("ShakeUnlock")
                .setContentText("Unlock service running")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        return super.onStartCommand(intent, Service.START_STICKY,startId);
    }
}
