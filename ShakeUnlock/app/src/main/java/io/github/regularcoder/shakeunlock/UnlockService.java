package io.github.regularcoder.shakeunlock;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class UnlockService extends IntentService {
    public UnlockService() {
        super("UnlockService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }


    public Handler handler = null;
    public static Runnable runnable = null;
    public Context t = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Toast.makeText(t, "Service is still running", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);

        return super.onStartCommand(intent, Service.START_NOT_STICKY,startId);
    }
}
