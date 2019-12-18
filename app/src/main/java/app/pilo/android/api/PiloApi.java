package app.pilo.android.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PiloApi {

    private static final String BASE_URL = "https://api.pilo.app/api/v1/panel/";
    static final String HOME_GET = BASE_URL + "home/get";
    static final String BROWSER_GET = BASE_URL + "browser/get";
    static final String SEARCH = BASE_URL + "search/";
    static final String LOGIN = BASE_URL + "login";
    static final String REGISTER = BASE_URL + "register";
    static final String ME = BASE_URL + "me";
    static final String UPDATE_PROFILE = BASE_URL + "me";
    static final String ALBUM_GET = BASE_URL + "album/";
    static final String ALBUMS_GET = BASE_URL + "albums/";
    static final String VIDEO_GET = "https://api.twitter.com/1/";
    static final String VIDEOS_GET = "https://api.twitter.com/1/";
    static final String MUSICS_GET = BASE_URL + "musics/";
    static final String ARTIST_GET = BASE_URL + "artist/";
    static final String ARTISTS_GET = BASE_URL + "artists/";
    static final String LIKES_GET = BASE_URL + "likes";
    static final String BOOKMARKS_GET = BASE_URL + "bookmarks";
    static final String MESSAGES_GET = BASE_URL + "messages";
    static final String FORGOT_PASSWORD = BASE_URL + "password/create";
    static final String CONTACTUS = BASE_URL + "message/add";


    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
