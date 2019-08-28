package app.pilo.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPrefManager {
    private static final String USER_SHARED_PREF_NAME = "user_shared_pref";
    private SharedPreferences sharedPreferences;

    public UserSharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setUserToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void setLocal(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("local", token);
        editor.apply();
    }


    public String getUserToken() {
        return sharedPreferences.getString("token", "");
    }

    public boolean getLocal() {
        return sharedPreferences.getBoolean("local", false);
    }
}
