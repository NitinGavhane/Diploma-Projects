package host.timekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class TipsActivity extends AppCompatActivity {

    Context ctx;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        ctx = this;
    }

    public void timeMGMT(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Time_management"));
        startActivity(browserIntent);
    }

    public void eisenHower(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.eisenhower.me/eisenhower-matrix/"));
        startActivity(browserIntent);
    }

    public void eatThatFrog(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@alltopstartups/eat-the-frogs-first-thing-in-the-morning-and-other-better-work-habits-7070f9e79822"));
        startActivity(browserIntent);
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

            case R.id.stopwatchNav:
                i = new Intent(ctx,StopwatchActivity.class);
                break;

            case R.id.profileNav:
                i = new Intent(ctx, ProfileActivity.class);
                break;

            case R.id.tipsNav:
            default: return;
        }


        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }


}
