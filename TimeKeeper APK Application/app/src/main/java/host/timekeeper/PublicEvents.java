package host.timekeeper;


import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;

public class PublicEvents {
    private String name, description, venue, vLat, vLong,uid;
    private boolean authrised;

    public boolean isAuthrised() {
        return authrised;
    }

    public void setAuthrised(boolean authrised) {
        this.authrised = authrised;
    }

    private Long time;
    private ArrayList<String> subscribers;

    @Exclude
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    private String eid;

    public PublicEvents() {

    }

    public ArrayList<String> getSubscribers() {

        return subscribers;
    }

    public void setSubscribers(ArrayList<String> subscribers) {
        this.subscribers = subscribers;
    }

    public PublicEvents(String name, String description, String venue, String vLat, String vLong, Date d, String uid,boolean authrised){
        if(subscribers==null)
            subscribers = new ArrayList<String>();
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.vLat = vLat;
        this.vLong = vLong;
        this.uid = uid;
        subscribers.add(uid);
        time = d.getTime();
        this.authrised = authrised;
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


    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
