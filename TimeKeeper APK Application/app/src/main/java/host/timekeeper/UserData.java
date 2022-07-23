package host.timekeeper;

public class UserData {

    private String name,phone;
    private boolean authorised;


    private int uevents,subs;

    public UserData(){

    }

    public UserData(String name, String phone, boolean authorised, int uevents, int subs){

        this.name = name;
        this.phone = phone;
        this.authorised = authorised;
        this.uevents = uevents;
        this.subs = subs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAuthorised() {
        return authorised;
    }

    public void setAuthorised(boolean authorised) {
        this.authorised = authorised;
    }

    public int getUevents() {
        return uevents;
    }

    public void setUevents(int uevents) {
        this.uevents = uevents;
    }

    public int getSubs() {
        return subs;
    }

    public void setSubs(int subs) {
        this.subs = subs;
    }





}
