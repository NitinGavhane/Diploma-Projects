package host.timekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {

    Context ctx;
    Intent i;

    TextView timerTV;
    Button startBTN;

    boolean running = false;
    boolean wasRunning = false;
    int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        ctx = this;
        timerTV = findViewById(R.id.timerTVAS);
        startBTN = findViewById(R.id.startBTNAS);
        runTimer();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = running;
        running = false;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }


    public void startStop(View view) {
        if(running){
            startBTN.setText("Start");
            running = false;
        } else {
            startBTN.setText("Stop");
            running = true;
        }
    }

    public void reset(View view) {
        seconds = 0;
        if(running){
            startBTN.setText("Start");
            running = false;
        }
    }

    private void runTimer()
    {

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run()
            {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);


                timerTV.setText(time);

                if (running) {
                    seconds++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }


    public void navigate(View view) {
        int id = view.getId();

        switch (id){
            case R.id.calendarNav:
                i = new Intent(ctx, CalendarActivity.class);
                break;

            case  R.id.timerNav:
                i = new Intent(ctx,TimerActivity.class);
                break;

            case R.id.profileNav:
                i = new Intent(ctx,ProfileActivity.class);
                break;

            case R.id.tipsNav:
                i = new Intent(ctx, TipsActivity.class);
                break;

            case R.id.stopwatchNav:
            default: return;
        }


        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }
}
