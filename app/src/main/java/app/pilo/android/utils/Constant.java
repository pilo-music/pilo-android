package app.pilo.android.utils;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int REPEAT_MODE_NONE = 1;
    public static final int REPEAT_MODE_ALL = 2;
    public static final int REPEAT_MODE_ONE = 3;


    public static final String URL_ABOUT_US_LOGO = "images/";

    public static final String TAG_ROOT = "ONLINE_MP3";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "msg";

    public static final String TAG_ID = "id";
    public static final String TAG_CAT_ID = "cat_id";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_MP3_URL = "mp3_url";
    public static final String TAG_DURATION = "mp3_duration";
    public static final String TAG_SONG_NAME = "mp3_title";
    public static final String TAG_DESC = "mp3_description";
    public static final String TAG_THUMB_B = "mp3_thumbnail_b";
    public static final String TAG_THUMB_S = "mp3_thumbnail_s";
    public static final String TAG_ARTIST = "mp3_artist";
    public static final String TAG_TOTAL_RATE = "total_rate";
    public static final String TAG_AVG_RATE = "rate_avg";
    public static final String TAG_USER_RATE = "user_rate";
    public static final String TAG_VIEWS = "total_views";
    public static final String TAG_DOWNLOADS = "total_download";

    public static final String TAG_CID = "cid";
    public static final String TAG_AID = "aid";
    public static final String TAG_PID = "pid";
    public static final String TAG_BID = "bid";

    public static final String TAG_BANNER_TITLE = "banner_title";
    public static final String TAG_BANNER_DESC = "banner_sort_info";
    public static final String TAG_BANNER_IMAGE = "banner_image";
    public static final String TAG_BANNER_TOTAL = "total_songs";

    public static final String TAG_ARTIST_NAME = "artist_name";
    public static final String TAG_ARTIST_IMAGE = "artist_image";
    public static final String TAG_ARTIST_THUMB = "artist_image_thumb";

    public static final String TAG_ALBUM_NAME = "album_name";
    public static final String TAG_ALBUM_IMAGE = "album_image";
    public static final String TAG_ALBUM_THUMB = "album_image_thumb";

    public static final String TAG_PLAYLIST_NAME = "playlist_name";
    public static final String TAG_PLAYLIST_IMAGE = "playlist_image";
    public static final String TAG_PLAYLIST_THUMB = "playlist_image_thumb";

    public static Boolean isLogged = false;

    public static int playPos = 0;
    public static Boolean isNewAdded = true;
    public static String addedFrom = "";
    public static ArrayList<Music> arrayList_play = new ArrayList<>();
    public static ArrayList<Music> arrayListOfflineSongs = new ArrayList<>();
    public static ArrayList<Album> arrayListOfflineAlbums = new ArrayList<>();
    public static ArrayList<Artist> arrayListOfflineArtist = new ArrayList<>();

    public static Boolean isRepeat = false, isSuffle = false,
            isPlayed = false, isFromNoti = false, isFromPush = false, isAppOpen = false, isOnline = true, isDownloaded = false, isBannerAd = true,
            isInterAd = true, isSongDownload = false, isUpdate = false;
    public static Context context;
    public static String recentLimit = "30";
    public static String packageName, pushSID = "0", pushCID = "0", pushCName = "", pushArtID = "0", pushArtNAME = "", pushAlbID = "0", pushAlbNAME = "", search_item = "";

    public static int rotateSpeed = 25000; //in milli seconds

    public static int bannerAdShowTime = 3000; //in milli seconds

    public static int adCount = 0;
    public static int adDisplay = 5;

    public static String ad_publisher_id = "";
    public static String ad_banner_id = "";
    public static String ad_inter_id = "";

    public static Boolean isLoginOn = true;
}