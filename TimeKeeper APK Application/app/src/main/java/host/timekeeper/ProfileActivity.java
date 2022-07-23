package host.timekeeper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    Context ctx;
    Intent i;

    FirebaseAuth mAuth;
    TextView name, uEvents, subEvents;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ctx = this;
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.nameTVAP);
        uEvents = findViewById(R.id.ueventsTVAP);
        subEvents = findViewById(R.id.subEventsTVAP);

        user = User.getInstance();
        user.id = mAuth.getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(user.id);

        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ctx, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d("ProfileActivity", e.toString());
                    return;
                }
                if(documentSnapshot.exists()){

                    user.data = documentSnapshot.toObject(UserData.class);
                    name.setText(user.data.getName());
                    uEvents.setText(user.data.getUevents()+" User Events");
                    subEvents.setText(user.data.getSubs()+" Subscribed Events");


                }else{
                    Toast.makeText(ctx,"User does not exist",Toast.LENGTH_SHORT).show();
                }
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

            case R.id.stopwatchNav:
                i = new Intent(ctx,StopwatchActivity.class);
                break;

            case R.id.tipsNav:
                i = new Intent(ctx, TipsActivity.class);
                break;

            case R.id.profileNav:
            default: return;
        }


        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }

    public void attemptLogout(View view) {
        mAuth.signOut();
        Toast.makeText(ctx,"Logged Out Successfully",Toast.LENGTH_SHORT).show();

        i = new Intent(ctx, LoginActivity.class);
        startActivity(i);

        finish();
    }
}
