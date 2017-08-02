package urmc.drinkingapp.control;

import android.text.TextUtils;

/**
 * Created by Shengqi Suizhu on 2017/8/2.
 * storing funtions that are used for login validation
 */

public class LoginAuthentication {
    public static boolean isValidEmail(CharSequence target) {
        return target != null &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // TODO: 2017/8/2 better rule for password
    public static boolean isValidPassword(CharSequence target) {
        return target != null &&
                target.toString().length() >= 6;
    }
}