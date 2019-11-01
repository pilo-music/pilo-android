package app.pilo.android.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.SingleAlbum;

public class AlbumApi {
    private Context context;

    public AlbumApi(Context context) {
        this.context = context;
    }

    public void get(String artist, int page, final RequestHandler.RequestHandlerWithList<Album> requestHandler) {
        String url;
        if (artist != null && !artist.equals("")) {
            url = PiloApi.ALBUMS_GET + "artist/" + artist + "/";
        } else
            url = PiloApi.ALBUMS_GET;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + page, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            List<Album> albums = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Album album = JsonParser.albumJsonParser(data.getJSONObject(i));
                                if (album != null)
                                    albums.add(album);
                            }
                            requestHandler.onGetInfo(status, albums);
                        } else
                            requestHandler.onGetInfo(status, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);

    }


    public void single(String slug, final RequestHandler.RequestHandlerWithModel<SingleAlbum> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ALBUM_GET + slug, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String status = response.getString("status");
                        SingleAlbum singleAlbum = parsAlbumApiData(data);
                        if (singleAlbum != null)
                            requestHandler.onGetInfo(status, singleAlbum);
                        else
                            requestHandler.onGetError(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    private SingleAlbum parsAlbumApiData(JSONObject data) throws JSONException {

        SingleAlbum singleAlbum = new SingleAlbum();
        List<Playlist> playlists = new ArrayList<>();

        //parse album
        Album album = JsonParser.albumJsonParser(data.getJSONObject("album"));


        // parse playlist
        JSONArray playlistJsonArray = data.getJSONArray("playlist");
        for (int i = 0; i < playlistJsonArray.length(); i++) {
            Playlist playlist = JsonParser.playlistJsonParser(playlistJsonArray.getJSONObject(i));
            if (playlist != null)
                playlists.add(playlist);
        }

        singleAlbum.setAlbum(album);
        singleAlbum.setPlaylists(playlists);

        return singleAlbum;
    }


}
