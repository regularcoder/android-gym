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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.countdown) TextView countdown;
    @BindView(R.id.stop_button) Button stop_button;
    @BindView(R.id.reset_button) Button reset_button;
    @BindView(R.id.start_button) Button start_button;

    private static final String STATE_MILLIS = "millis";
    private static final String STATE_ISRUNNING = "isrunning";

    private final long countDownMinutes = 7;

    long startMillis;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            startMillis = savedInstanceState.getLong(STATE_MILLIS);

            //Resume timer if it was running prior to orientation change
            if(savedInstanceState.getBoolean(STATE_ISRUNNING)) {
                startTimer();
            }
        }
        else {
            startMillis = countDownMinutes * 60 * 1000;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateTimerText(startMillis);

        //Prevent button text from getting automatically capitalized
        stop_button.setTransformationMethod(null);
        start_button.setTransformationMethod(null);
        reset_button.setTransformationMethod(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);

        // Save state in case of orientation changes
        outState.putLong(STATE_MILLIS, startMillis);

        Boolean timerRunning;
        if(timer == null) {
            timerRunning = false;
        }
        else {
            timerRunning = true;
        }
        outState.putBoolean(STATE_ISRUNNING, timerRunning);
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
        long secondsRemaming = millisUntilFinished / 1000;

        countdown.setText(String.format("%02d:%02d", secondsRemaming / 60, secondsRemaming % 60));
    }

    // Starts timer
    public void startButtonClick(View view) {
        startTimer();
    }

    private void startTimer() {
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
