package app.pilo.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import app.pilo.android.services.MusicPlayer.Constant;

public class UserSharedPrefManager {
    private static final String USER_SHARED_PREF_NAME = "user_shared_pref";
    private static final String LOCAL = "local";
    private static final String ACTIVE_MUSIC_SLUG = "active_music_slug";
    private static final String REPEAT_MODE = "repeat_mode";
    private static final String SHUFFLE_MODE = "shuffle_mode";
    private static final String STREAM_QUALITY = "STREAM_QUALITY";
    private static final String DOWNLOAD_QUALITY = "STREAM_QUALITY";
    private static final String FIRST_LUNCH = "FIRST_LUNCH";

    private SharedPreferences sharedPreferences;

    public UserSharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setFirstLunch(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LUNCH, value);
        editor.apply();
    }

    public boolean getFirstLunch() {
        return sharedPreferences.getBoolean(FIRST_LUNCH, true);
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

    //----------DOWNLOAD_QUALITY  -----------------------
    public void setDownloadQuality(String downloadQuality) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOWNLOAD_QUALITY, downloadQuality);
        editor.apply();
    }

    public String getDownloadQuality() {
        return sharedPreferences.getString(DOWNLOAD_QUALITY, "320");
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

    //----------SHUFFLE_MODE-----------------------
    public void setShuffleMode(boolean shuffleMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHUFFLE_MODE, shuffleMode);
        editor.apply();
    }

    public boolean getShuffleMode() {
        return sharedPreferences.getBoolean(SHUFFLE_MODE, false);
    }

}
