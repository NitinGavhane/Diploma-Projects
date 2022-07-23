package host.timekeeper;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DailyListAdapter extends ArrayAdapter<String>{


    private final Activity context;
    private final String[] date;

    DailyListAdapter(Activity ctx, String[] date){
        super(ctx,R.layout.daily_list_lv,date);
        context = ctx;
        this.date = date;
    }


    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.daily_list_lv, null, true);
        TextView dateTV = row.findViewById(R.id.dateTVDLL);
        dateTV.setText(date[position]);
        return row;
    }
}
