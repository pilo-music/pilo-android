package app.pilo.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPrefManager {
    private static final String USER_SHARED_PREF_NAME = "user_shared_pref";
    private static final String LOCAL = "local";
    private SharedPreferences sharedPreferences;

    public UserSharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLocal(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL, token);
        editor.apply();
    }

    public String getLocal() {
        return sharedPreferences.getString(LOCAL, "fa");
    }
}
