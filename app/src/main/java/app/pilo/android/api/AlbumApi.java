package app.pilo.android.api;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.SingleAlbum;
import cz.msebera.android.httpclient.Header;

public class AlbumApi {

    public void get(String artist, int page, final RequestHandler.RequestHandlerWithList<Album> requestHandler) {
        String url;
        if (artist != null && !artist.equals("")) {
            url = PiloApi.ALBUMS_GET + "artist/" + artist + "/";
        } else
            url = PiloApi.ALBUMS_GET;

        PiloApi.get(url + page, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    String status = response.getString("status");
                    List<Album> albums = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        Album album = JsonParser.albumJsonParser(data.getJSONObject(i));
                        if (album != null)
                            albums.add(album);
                    }

                    requestHandler.onGetInfo(status, albums);
                } catch (JSONException e) {
                    e.printStackTrace();
                    requestHandler.onGetError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestHandler.onGetError();
            }
        });
    }


    public void single(String slug, final RequestHandler.RequestHandlerWithModel<SingleAlbum> requestHandler) {
        PiloApi.get(PiloApi.ALBUM_GET + slug, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String status = response.getString("status");
                    SingleAlbum singleAlbum = parsAlbumApiData(data);
                    if (singleAlbum != null)
                        requestHandler.onGetInfo(status, singleAlbum);
                    else
                        requestHandler.onGetError();
                } catch (JSONException e) {
                    e.printStackTrace();
                    requestHandler.onGetError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestHandler.onGetError();
            }
        });
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
