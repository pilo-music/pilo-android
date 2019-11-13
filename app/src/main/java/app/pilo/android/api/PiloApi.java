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
    static final String MUSIC_GET = "https://api.twitter.com/1/";
    static final String MUSICS_GET = "https://api.twitter.com/1/";
    static final String ALBUM_GET = BASE_URL + "album/";
    static final String ALBUMS_GET = BASE_URL + "albums/";
    static final String VIDEO_GET = "https://api.twitter.com/1/";
    static final String VIDEOS_GET = "https://api.twitter.com/1/";
    static final String ARTIST_GET = BASE_URL + "artist/";
    static final String ARTISTS_GET = BASE_URL + "artists/";
    static final String FORGOT_PASSWORD = BASE_URL + "password/create";


    public static SimpleExoPlayer exoPlayer;
    public static Context context;
    public static int columnWidth = 0;
    public static int playPos = 0;
    public static ArrayList<Music> arrayList_play = new ArrayList<>();
    public static Boolean isRepeat = false;
    public static boolean isSuffle = false;
    public static boolean isPlaying = false;
    public static boolean isFav = false;
    public static boolean isAppFirst = true;
    public static boolean isPlayed = false;
    public static boolean isFromNoti = false;
    public static boolean isFromPush = false;
    public static boolean isAppOpen = false;
    public static boolean isOnline = true;
    public static boolean isBannerAd = true;
    public static boolean isInterAd = true;
    public static boolean isSongDownload = false;
    public static boolean isBannerAdCalled = false;
    public static int volume = 25;
    public static String frag = "", pushSID = "0", pushCID = "0", pushCName = "", pushAID = "0", pushANAME = "", search_item = "";

    public static String loadedSongPage = "";
    public static int rotateSpeed = 25000; //in milli seconds


    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
