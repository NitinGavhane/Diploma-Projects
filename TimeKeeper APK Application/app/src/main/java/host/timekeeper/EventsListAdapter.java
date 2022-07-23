package host.timekeeper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Context ctx;
    private final String[] names;
    private final EventData[] events;

    EventsListAdapter(Activity ctx, String[] names, EventData events[]) {
        super(ctx, R.layout.event_lv, names);
        this.context = ctx;
        this.names = names;
        this.events = events;
        this.ctx = ctx;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View row = inflater.inflate(R.layout.event_lv, null, true);
        TextView dateTV = row.findViewById(R.id.eventNameTV);

        dateTV.setText(names[position]);

        final View dialogLayout = context.getLayoutInflater().inflate(R.layout.uevent_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("User Event");

        builder.setView(dialogLayout);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();

        final TextView name = dialogLayout.findViewById(R.id.uedNameTV);
        final TextView desc = dialogLayout.findViewById(R.id.uedDescTV);
        final TextView venue = dialogLayout.findViewById(R.id.uedVenueTV);

        final Button mapBTN = dialogLayout.findViewById(R.id.mapBTN);
        final Button delBTN = dialogLayout.findViewById(R.id.deleteBTN);


        final LinearLayout eventItem = row.findViewById(R.id.eventItem);
        eventItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EventData ev = events[position];
                name.setText("Name : " + ev.getName());
                desc.setText("Details : " + ev.getDescription());
                venue.setText("Venue : " + ev.getVenue());

                if (!TextUtils.isEmpty(ev.getvLat()) && !TextUtils.isEmpty(ev.getvLong())) {
                    mapBTN.setText("Open Directions");

                    mapBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+ev.getvLat()+","+ev.getvLong());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try {
                                context.startActivity(mapIntent);
                            } catch (Exception e) {
                                Toast.makeText(ctx, "Google maps is not installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    mapBTN.setText("Search in maps");
                    mapBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ev.getVenue());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try {
                                context. startActivity(mapIntent);
                            } catch (Exception e) {
                                Toast.makeText(ctx, "Google maps is not installed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                    delBTN.setText("Delete");

                delBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            FirebaseFirestore.getInstance().collection("Events").document(ev.getEid())
                                    .delete();
                            dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return row;
    }
}
