package host.timekeeper;

import android.text.TextUtils;
import android.util.Patterns;

public class Helpers {

    public static boolean isValidEmail(String email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPass(String password){
        return !(password.length()<6);
    }


    public static boolean isValidName(String name){
        return (!TextUtils.isEmpty(name) );
    }

    public static boolean isValidPhone(String phone){
        return (TextUtils.isDigitsOnly(phone)&&phone.length() == 10);
    }
}
