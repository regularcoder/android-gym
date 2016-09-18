package io.github.regularcoder.plainpomo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    CountDownTimer timer;

    long countDownMinutes = 1;

    long startMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startMillis = countDownMinutes * 60 * 1000;
        updateTimerText(startMillis);

        //Prevent button text from getting automatically capitalized
        Button stop_button = (Button) findViewById(R.id.stop_button);
        stop_button.setTransformationMethod(null);

        Button start_button = (Button) findViewById(R.id.start_button);
        start_button.setTransformationMethod(null);

        Button reset_button = (Button) findViewById(R.id.reset_button);
        reset_button.setTransformationMethod(null);
    }

    // Starts timer
    public void resetTimer(View view) {
        startMillis = countDownMinutes * 60 * 1000;

        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        updateTimerText(startMillis);
    }

    //Update timer text
    private void updateTimerText(long millisUntilFinished) {
        TextView countDown = (TextView) findViewById(R.id.countdown);

        long secondsRemaming = millisUntilFinished / 1000;

        countDown.setText(String.format("%02d:%02d", secondsRemaming / 60, secondsRemaming % 60));
    }

    // Starts timer
    public void startTimer(View view) {
        if(timer == null) {
            timer = new CountDownTimer(startMillis, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    startMillis = millisUntilFinished;
                    updateTimerText(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    updateTimerText(0);

                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

                    // Vibrate for 400 milliseconds
                    v.vibrate(400);
                    //countDown.setText("done!");
                }
            }.start();
        }
    }

    // Stops timer
    public void stopTimer(View view) {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
