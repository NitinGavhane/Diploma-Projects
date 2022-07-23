package host.timekeeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    Context ctx;
    Intent i;

    Calendar cal;
    CalendarView calV;
    ListView dailyView;
    Event e;
    User user;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ctx = this;
        calV = findViewById(R.id.calendarCVCA);
        dailyView = findViewById(R.id.dailyLVCA);

        mAuth = FirebaseAuth.getInstance();
        e = Event.getInstance();

        user = User.getInstance();
        user.id = mAuth.getCurrentUser().getUid();

        calV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                cal = Calendar.getInstance();
                cal.set(year,month,day);
                cal.set(java.util.Calendar.HOUR_OF_DAY,0);
                cal.set(java.util.Calendar.MINUTE,0);
                cal.set(java.util.Calendar.SECOND,0);
                cal.set(java.util.Calendar.MILLISECOND,0);

                openDetailsView(cal.getTime());
            }
        });

        DailyListAdapter adapter = new DailyListAdapter(this, getMonth());
        dailyView.setAdapter(adapter);

        dailyView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position,long id){
                openDetailsView(position+1);
            }
        });
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
                }else{
                    Toast.makeText(ctx,"User does not exist",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openDetailsView(int day){
        cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DATE,day);
        cal.set(java.util.Calendar.HOUR_OF_DAY,0);
        cal.set(java.util.Calendar.MINUTE,0);
        cal.set(java.util.Calendar.SECOND,0);
        cal.set(java.util.Calendar.MILLISECOND,0);

        i = new Intent(ctx,DetailsActivity.class);
        i.putExtra("DATE", cal.getTime().getTime());

        startActivity(i);
    }


    public void openDetailsView(Date date){

        i = new Intent(ctx,DetailsActivity.class);
        i.putExtra("DATE", date.getTime());
        startActivity(i);
    }

    private String[] getMonth(){

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        calendar.set(java.util.Calendar.DATE,1);

        int days =  calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        String date="";
        String[] output = new String[days];

        for(int i=0;i<days;i++){
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            date = formatter.format(calendar.getTime());
            calendar.add(java.util.Calendar.DATE,1);
            output[i] = date;
        }
        return output;
    }


    public void addEvent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Event");

        final View inputLayout = getLayoutInflater().inflate(R.layout.event_input_dialog, null);
        builder.setView(inputLayout);

        final Button pubBTN,timeBTN, addBTN;

        pubBTN = inputLayout.findViewById(R.id.eventPublicEA);
        timeBTN = inputLayout.findViewById(R.id.eventTimeEA);
        addBTN = inputLayout.findViewById(R.id.setEventBTN);


        timeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
                setDate();

            }
        });

        pubBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e.isPublic()){
                    pubBTN.setText("Set Public");
                    e.setPublic(false);
                } else{
                    pubBTN.setText("Set Private");
                    e.setPublic(true);
                }
            }
        });


        final AlertDialog dialog = builder.create();

       addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameET,descET,venueET,vLatET,vLongET;
                String name,desc,venue,vlat,vlong;

                nameET = inputLayout.findViewById(R.id.eventNameEA);
                descET = inputLayout.findViewById(R.id.eventDescEA);
                venueET = inputLayout.findViewById(R.id.eventVenueEA);
                vLatET = inputLayout.findViewById(R.id.eventLatEA);
                vLongET = inputLayout.findViewById(R.id.eventLongEA);

                name = nameET.getText().toString();
                desc = descET.getText().toString();
                venue = venueET.getText().toString();
                vlat = vLatET.getText().toString();
                vlong = vLongET.getText().toString();

                boolean err = false;

                if (!Helpers.isValidName(name)) {
                    nameET.setError("Name is required");
                    err = true;
                }

                if (!Helpers.isValidName(desc)) {
                    descET.setError("Description is required");
                    err = true;
                }

                if (!Helpers.isValidName(venue)) {
                    venueET.setError("Venue is required");
                    err = true;
                }
                if(err) return;
                e.setEvent(name,desc,venue,vlat,vlong,user.id,user.data.isAuthorised());
                uploadEvent();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void uploadEvent(){
        if(e.isPublic()){
            FirebaseFirestore.getInstance().collection("PublicEvents").add(e.ep)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ctx,"Added",Toast.LENGTH_SHORT).show();
                            }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            FirebaseFirestore.getInstance().collection("Events").add(e.ed)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ctx,"Added",Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
        e.reset();
    }

    private void setDate() {

        int mYear,mMonth,mDay;
        java.util.Calendar c = java.util.Calendar.getInstance();
        mYear = c.get(java.util.Calendar.YEAR);
        mMonth = c.get(java.util.Calendar.MONTH);
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);


        final DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        e.setDate(year, monthOfYear, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void setTime(){
        int mHour,mMinute;
        final java.util.Calendar c = java.util.Calendar.getInstance();
        mHour = c.get(java.util.Calendar.HOUR_OF_DAY);
        mMinute = c.get(java.util.Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        e.setTime(hourOfDay,minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void navigate(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.profileNav:
                i = new Intent(ctx, ProfileActivity.class);
                break;

            case R.id.timerNav:
                i = new Intent(ctx, TimerActivity.class);
                break;

            case R.id.stopwatchNav:
                i = new Intent(ctx, StopwatchActivity.class);
                break;

            case R.id.tipsNav:
                i = new Intent(ctx, TipsActivity.class);
                break;

            case R.id.calendarNav:
            default:
                return;
        }


        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

}
