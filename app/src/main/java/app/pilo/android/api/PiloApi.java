package app.pilo.android.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

import app.pilo.android.models.Music;

public class PiloApi {

    private static final String BASE_URL = "https://api.pilo.app/api/v1/panel/";
    static final String HOME_GET = BASE_URL + "home/get";
    static final String BROWSER_GET = BASE_URL + "browser/get";
    static final String SEARCH = BASE_URL + "search/";
    static final String LOGIN = BASE_URL + "login";
    static final String REGISTER = BASE_URL + "register";
    static final String ME = BASE_URL + "me";
    static final String MUSIC_GET = "https://api.twitter.com/1/";
    static final String MUSICS_GET = "https://api.twitter.com/1/";
    static final String ALBUM_GET = BASE_URL + "album/";
    static final String ALBUMS_GET = BASE_URL + "albums/";
    static final String VIDEO_GET = "https://api.twitter.com/1/";
    static final String VIDEOS_GET = "https://api.twitter.com/1/";
    static final String ARTIST_GET = BASE_URL + "artist/";
    static final String ARTISTS_GET = BASE_URL + "artists/";
    static final String FORGOT_PASSWORD = BASE_URL + "password/create";


    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
