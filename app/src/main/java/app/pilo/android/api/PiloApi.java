package app.pilo.android.api;

import android.content.Context;
import android.net.Uri;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.util.Map;

import app.pilo.android.helpers.Singleton;

class PiloApi {

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

    static void post(Context context, String url, RequestParams params, Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                client.addHeader(entry.getKey(), entry.getValue());
            }
        }
        client.post(context,url, params, responseHandler);

    }

}
