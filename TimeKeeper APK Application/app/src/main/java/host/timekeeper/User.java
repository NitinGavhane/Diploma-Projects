package host.timekeeper;

public class User {
    private static User user;
    public String id;
    public UserData data;

    private User(){
    }

    public static User getInstance(){
        if(user == null){
            user = new User();
        }
        return user;
    }

    public void setUser(String name,String phone){
        data = new UserData(name,phone,false,0,0);
    }


}
