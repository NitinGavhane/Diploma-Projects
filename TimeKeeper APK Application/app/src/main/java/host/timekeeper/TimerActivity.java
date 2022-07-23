package host.timekeeper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    Context ctx;
    Intent i;

    Button startBTN;
    TextView timerTV;

    int time;
    boolean running = false, timerSet = false;


    CTD timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ctx = this;
        timerTV = findViewById(R.id.timerTVAT);
        startBTN = findViewById(R.id.startBTNAT);

    }

    public void startStop(View view) {

        if(!timerSet){
            takeInput();
        }
        else{
            if(running){
                startBTN.setText("Start");
                timerTV.setText(String.format(Locale.getDefault(), "%02d:%02d", time/60, time%60));
                running = false;
                timer.cancel();
            }
            else {
                startBTN.setText("Stop");
                running = true;
                timer = new CTD(time*1000, 1000);
                timer.start();
            }
        }
    }

    public void reset(View view) {

        if(running){
            running = false;
            timer.cancel();
            startBTN.setText("Start");
        }

        timerTV.setText("00:00");
        time = 0;
        timerSet = false;
    }

    public void takeInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Time");

        final View inputLayout = getLayoutInflater().inflate(R.layout.timer_input_dialog, null);
        builder.setView(inputLayout);

        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText minutesET = inputLayout.findViewById(R.id.minutes);
                EditText secondsET = inputLayout.findViewById(R.id.seconds);
                setTimer(minutesET.getText().toString(), secondsET.getText().toString());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void timerFinished() {

        if(running){
            startBTN.setText("Start");
            timerTV.setText(String.format(Locale.getDefault(), "%02d:%02d", time/60, time%60));
            running = false;
        }

        Toast.makeText(ctx,"Timer Finished",Toast.LENGTH_SHORT).show();
    }

    private void setTimer(String minutes, String seconds) {
        try{

            int mins,secs;

            mins = (minutes.length() == 0) ? 0: Integer.parseInt(minutes);
            secs = (seconds.length() == 0 )? 0: Integer.parseInt(seconds);

            if(mins>60 && mins < 0) {
                Toast.makeText(ctx, "Invalid Time", Toast.LENGTH_SHORT).show();
                return;
            }

            if(secs<0){
                Toast.makeText(ctx, "Invalid Time", Toast.LENGTH_SHORT).show();
                return;
            }

            mins += (int) (secs/60);
            secs = (secs%60);

            time = mins*60+secs;
            if(time > 0) {
                timerTV.setText(String.format(Locale.getDefault(), "%02d:%02d", time / 60, time % 60));
                timerSet = true;
            }
        }catch(Exception e){

            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void navigate(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.calendarNav:
                i = new Intent(ctx, CalendarActivity.class);
                break;

            case R.id.stopwatchNav:
                i = new Intent(ctx, StopwatchActivity.class);
                break;

            case R.id.profileNav:
                i = new Intent(ctx, ProfileActivity.class);
                break;

            case R.id.tipsNav:
                i = new Intent(ctx, TipsActivity.class);
                break;

            case R.id.timerNav:
            default:
                return;

        }

        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }

    public class CTD extends CountDownTimer {

        public CTD(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int seconds = (int) (millisUntilFinished/1000);
            String time = String.format(Locale.getDefault(), "%02d:%02d", seconds/60, seconds%60);
            timerTV.setText(time);
        }

        @Override
        public void onFinish() {
            timerFinished();
        }
    }

}
