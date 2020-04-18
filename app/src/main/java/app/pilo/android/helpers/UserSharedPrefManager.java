package app.pilo.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import app.pilo.android.utils.Constant;

public class UserSharedPrefManager {
    private static final String USER_SHARED_PREF_NAME = "user_shared_pref";
    private static final String LOCAL = "local";
    private static final String ACTIVE_MUSIC_SLUG = "active_music_slug";
    private static final String REPEAT_MODE = "repeat_mode";
    private static final String STREAM_QUALITY = "STREAM_QUALITY";

    private SharedPreferences sharedPreferences;

    public UserSharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    //----------LOCAL-----------------------

    public void setLocal(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL, token);
        editor.apply();
    }

    public String getLocal() {
        return sharedPreferences.getString(LOCAL, "fa");
    }

    //----------ACTIVE_MUSIC_ID-----------------------
    public void setActiveMusicSlug(String activeMusicSlug) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACTIVE_MUSIC_SLUG, activeMusicSlug);
        editor.apply();
    }

    public String getActiveMusicSlug() {
        return sharedPreferences.getString(ACTIVE_MUSIC_SLUG, "");
    }

    //----------STREAM_QUALITY-----------------------
    public void setStreamQuality(String streamQuality) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STREAM_QUALITY, streamQuality);
        editor.apply();
    }

    public String getStreamQuality() {
        return sharedPreferences.getString(STREAM_QUALITY, "320");
    }

    //----------REPEAT_MODE-----------------------
    public void setRepeatMode(int repeatMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(REPEAT_MODE, repeatMode);
        editor.apply();
    }

    public int getRepeatMode() {
        return sharedPreferences.getInt(REPEAT_MODE, Constant.REPEAT_MODE_NONE);
    }

}
