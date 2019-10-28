package app.pilo.android.api;

import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import app.pilo.android.helpers.Singleton;

public class PiloApi {

    private static final String BASE_URL = "https://api.pilo.app/api/v1/panel/";
    static final String HOME_GET = BASE_URL + "home/get";
    static final String BROWSER_GET = BASE_URL + "browser/get";
    public static final String SEARCH = BASE_URL + "search/";
    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTER = BASE_URL + "register";
    public static final String ME = BASE_URL + "me";
    public static final String MUSIC_GET = "https://api.twitter.com/1/";
    public static final String MUSICS_GET = "https://api.twitter.com/1/";
    public static final String ALBUM_GET = BASE_URL + "album/";
    public static final String ALBUMS_GET = BASE_URL + "albums/";
    public static final String VIDEO_GET = "https://api.twitter.com/1/";
    public static final String VIDEOS_GET = "https://api.twitter.com/1/";
    public static final String ARTIST_GET = BASE_URL + "artist/";
    public static final String ARTISTS_GET = BASE_URL + "artists/";


    private static AsyncHttpClient client = Singleton.getAsyncHttpInstance();


    static void get(String url, RequestParams params, Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                client.addHeader(entry.getKey(), entry.getValue());
            }
        }
        client.get(Uri.encode(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                client.addHeader(entry.getKey(), entry.getValue());
            }
        }
        client.post(Uri.encode(url), params, responseHandler);

    }

}
