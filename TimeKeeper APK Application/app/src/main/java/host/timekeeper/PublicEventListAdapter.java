package host.timekeeper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
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

import java.util.ArrayList;

public class PublicEventListAdapter  extends ArrayAdapter<String> {

    private final Activity context;
    private final Context ctx;
    private final String[] names;
    private final PublicEvents[] events;


    PublicEventListAdapter(Activity ctx, String[] names, PublicEvents events[]){
        super(ctx,R.layout.event_lv,names);
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

        final View dialogLayout = context.getLayoutInflater().inflate(R.layout.pevent_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Public Event");

        builder.setView(dialogLayout);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();

        final TextView name = dialogLayout.findViewById(R.id.pedNameTV);
        final TextView desc = dialogLayout.findViewById(R.id.pedDescTV);
        final TextView auth = dialogLayout.findViewById(R.id.pedAuthTV);
        final TextView venue = dialogLayout.findViewById(R.id.pedVenueTV);

        final Button mapBTN = dialogLayout.findViewById(R.id.pmapBTN);
        final Button subUnSubBTN = dialogLayout.findViewById(R.id.psubunsubBTN);

        LinearLayout eventItem = row.findViewById(R.id.eventItem);
        eventItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PublicEvents ev = events[position];
                String authValue = (events[position].isAuthrised())?"Authorised":"Not Authorised";

                name.setText("Name : " + ev.getName());
                desc.setText("Details : " + ev.getDescription());
                auth.setText(authValue);
                venue.setText("Venue : " + ev.getVenue());

                final ArrayList<String> subscribers = ev.getSubscribers();

                if (!TextUtils.isEmpty(ev.getvLat()) && !TextUtils.isEmpty(ev.getvLong())) {
                    mapBTN.setText("Open Directions");

                    mapBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+events[position].getvLat()+","+events[position].getvLong());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try {
                                context.startActivity(mapIntent);
                            } catch (Exception e) {
                                Toast.makeText(ctx, "Google maps is not installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    mapBTN.setText("Search in maps");
                    mapBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ev.getVenue());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            try {
                                context.startActivity(mapIntent);
                            } catch (Exception e) {
                                Toast.makeText(ctx, "Google maps is not installed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(!subscribers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    subUnSubBTN.setText("Subscribe");
                }
                else if(ev.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                    subUnSubBTN.setText("Delete");
                } else {
                    subUnSubBTN.setText("Unsubscribe");
                }

                subUnSubBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(subUnSubBTN.getText().equals("Subscribe")){
                            FirebaseFirestore.getInstance().collection("PublicEvents")
                                    .document(ev.getEid())
                                    .update("subscribers", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            dialog.dismiss();
                        } else if(subUnSubBTN.getText().equals("Delete")){
                            FirebaseFirestore.getInstance().collection("PublicEvents")
                                    .document(ev.getEid()).delete();
                            dialog.dismiss();
                        } else{
                            FirebaseFirestore.getInstance().collection("PublicEvents")
                                    .document(ev.getEid())
                                    .update("subscribers", FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        return row;
    }
}