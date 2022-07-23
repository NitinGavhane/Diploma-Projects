package host.timekeeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {


    private TextView dateTVAD, dayTVAD, daysLeftTVAD;
    private ListView userEventsList, subEventsList;
    private Event e;

    Date date;
    CollectionReference publicEvents = FirebaseFirestore.getInstance().collection("PublicEvents");
    CollectionReference userEvents = FirebaseFirestore.getInstance().collection("Events");

    User user;
    Activity ctx;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        date = new Date();
        Intent i = getIntent();
        date.setTime(i.getLongExtra("DATE", Calendar.getInstance().getTime().getTime()));

        dateTVAD = findViewById(R.id.dateTVAD);
        dayTVAD = findViewById(R.id.dayTVAD);
        daysLeftTVAD = findViewById(R.id.daysLeftTVAD);
        dateTVAD.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));
        dayTVAD.setText(new SimpleDateFormat("EEEE").format(date));
        daysLeftTVAD.setText(getDiff(date));
        userEventsList = findViewById(R.id.userEventsLVAD);
        subEventsList = findViewById(R.id.subEventsLVAD);
        ctx = this;
        e = Event.getInstance();
        e.reset();


        mAuth = FirebaseAuth.getInstance();
        user = User.getInstance();
        user.id = mAuth.getCurrentUser().getUid();

    }

    @Override
    protected void onStart() {
        super.onStart();

        long[] range = getDateRange();
        userEvents
                .whereEqualTo("uid", user.id)
                .whereGreaterThanOrEqualTo("time", range[0])
                .whereLessThanOrEqualTo("time", range[1])
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        EventData[] ues = new EventData[queryDocumentSnapshots.size()];
                        String[] namesUE = new String[queryDocumentSnapshots.size()];
                        int i = 0;

                        for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
                            ues[i] = queryDocument.toObject(EventData.class);
                            ues[i].setEid(queryDocument.getId());
                            namesUE[i] = ues[i].getName();
                            i++;
                        }

                        EventsListAdapter adapter = new EventsListAdapter(ctx,namesUE,ues);
                        userEventsList.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Event Failue :", e.getMessage());
                    }
                });

        publicEvents
                .whereGreaterThanOrEqualTo("time", range[0])
                .whereLessThanOrEqualTo("time", range[1])
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        PublicEvents[] pes = new PublicEvents[queryDocumentSnapshots.size()];
                        String[] namesPE = new String[queryDocumentSnapshots.size()];
                        int i = 0;
                        for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
                            pes[i] = queryDocument.toObject(PublicEvents.class);
                            pes[i].setEid(queryDocument.getId());
                            namesPE[i] = pes[i].getName();
                            i++;
                        }
                        PublicEventListAdapter adapter = new PublicEventListAdapter(ctx,namesPE, pes);
                        subEventsList.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Event Failure :", e.getMessage());
                    }
                });

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


    private String getDiff(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        long diff = date.getTime() - today.getTime();

        diff = diff - (diff % (24 * 60 * 60 * 1000));
        diff = diff / (24 * 60 * 60 * 1000);

        int days = (int) diff;

        if (days < 0) {

            return (days * -1) + " days ago";
        } else if (days > 0) {

            return days + " days left";
        }
        return "Today";
    }

    public void backToCal(View view) {
        finish();
    }

    public long[] getDateRange() {
        long time = date.getTime();
        long from = time;
        long to = time + (24 * 60 * 60 * 1000);
        long[] range = {from, to};
        return range;
    }

    public void addUserEvent(View view) {
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

                            FirebaseFirestore.getInstance().collection("User").document(user.id)
                                    .update("uevents", user.data.getUevents()+1);
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

                            FirebaseFirestore.getInstance().collection("User").document(user.id)
                                    .update("subs", user.data.getSubs()+1);
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);


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

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        e.setTime(hourOfDay,minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
