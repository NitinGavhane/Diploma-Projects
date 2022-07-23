package host.timekeeper;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class EventData {

    private String name, description, venue, vLat, vLong,uid;
    private long time;
    private String eid;

    public EventData() {

    }

    @Exclude
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public EventData(String name, String description, String venue, String vLat, String vLong, Date d, String uid){

        this.name = name;
        this.description = description;
        this.venue = venue;
        this.vLat = vLat;
        this.vLong = vLong;
        this.uid = uid;
        time = d.getTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getvLat() {
        return vLat;
    }

    public void setvLat(String vLat) {
        this.vLat = vLat;
    }

    public String getvLong() {
        return vLong;
    }

    public void setvLong(String vLong) {
        this.vLong = vLong;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
