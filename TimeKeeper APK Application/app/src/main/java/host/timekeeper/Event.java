package host.timekeeper;

import java.util.Calendar;
import java.util.Date;

public class Event {

    static Event e = null;
    Calendar cal;
    EventData ed, eda[];
    PublicEvents ep, epa[];
    boolean pub;



    private Event() {
        cal = Calendar.getInstance();
        pub = false;
    }

    public static Event getInstance() {
        if (e == null)
            e = new Event();
        return e;
    }

    public void setTime(int hour, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public Date getTime() {
        return cal.getTime();
    }

    public void setDate(int year, int month, int date) {
        cal.set(year, month, date);
    }

    public void setPublic(boolean pub) {
        this.pub = pub;
    }

    public boolean isPublic() {
        return pub;
    }

    public void setEvent(String name, String description, String venue, String vLat, String vLong, String uid, boolean authorised) {
        if (pub) {
            ep = new PublicEvents(name, description, venue, vLat, vLong, cal.getTime(), uid, authorised);
        } else {
            ed = new EventData(name, description, venue, vLat, vLong, cal.getTime(), uid);
        }

    }

    public void reset(){

        cal = Calendar.getInstance();
        pub = false;
    }

    public void setUEArr(EventData[] eda){
        this.eda = eda;
    }

    public void setPEArr(PublicEvents[] epa){
        this.epa = epa;
    }
}