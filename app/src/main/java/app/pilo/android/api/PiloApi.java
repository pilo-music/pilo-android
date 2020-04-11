package app.pilo.android.api;

public class PiloApi {

    private static final String BASE_URL = "http://new.pilo.app/api/v1/";
    static final String VERSION = BASE_URL + "version";
    static final String HOME_GET = BASE_URL + "homes";
    static final String BROWSER_GET = BASE_URL + "browser/get";
    static final String SEARCH = BASE_URL + "search/";
    static final String LOGIN = BASE_URL + "login";
    static final String REGISTER = BASE_URL + "register";
    static final String VERIFY = BASE_URL + "verify";
    static final String ME = BASE_URL + "me";
    static final String UPDATE_PROFILE = BASE_URL + "me";
    static final String ALBUM_SINGLE = BASE_URL + "album/";
    static final String ALBUM_GET = BASE_URL + "albums/";
    static final String PLAYLIST_SINGLE = BASE_URL + "playlist/";
    static final String PLAYLIST_GET = BASE_URL + "playlists/";
    static final String VIDEO_GET = BASE_URL + "video/";
    static final String VIDEOS_GET = BASE_URL + "videos/";
    static final String MUSICS_GET = BASE_URL + "musics/";
    static final String MUSICS_SINGLE = BASE_URL + "music/";
    static final String ARTIST_SINGLE = BASE_URL + "artist/";
    static final String ARTIST_GET = BASE_URL + "artists/";
    static final String LIKE_GET = BASE_URL + "likes";
    static final String LIKE_ADD = BASE_URL + "like";
    static final String FOLLOW_GET = BASE_URL + "follows";
    static final String FOLLOW_ADD = BASE_URL + "follow";
    static final String BOOKMARK_GET = BASE_URL + "bookmarks";
    static final String BOOKMARK_ADD = BASE_URL + "bookmarks";
    static final String MESSAGE_GET = BASE_URL + "messages";
    static final String MESSAGE_ADD = BASE_URL + "message";
    static final String FORGOT_PASSWORD_CREATE = BASE_URL + "forgot-passport/create";
    static final String FORGOT_PASSWORD = BASE_URL + "forgot-passport/reset";
    static final String CONTACTUS = BASE_URL + "message/add";

}
