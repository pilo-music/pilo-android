package app.pilo.android.api;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Search;
import app.pilo.android.models.Video;
import cz.msebera.android.httpclient.Header;

public class SearchApi {
    public void get(String text, final RequestHandler.RequestHandlerWithModel<Search> requestHandler) {
        PiloApi.get(PiloApi.SEARCH + text, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String status = response.getString("status");
                    Search search = parsSearchApiData(data);
                    if (search != null)
                        requestHandler.onGetInfo(status, search);
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

    private Search parsSearchApiData(JSONObject data) throws JSONException {
        Search search = new Search();
        List<Album> albums = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        List<Video> videos = new ArrayList<>();
        List<Music> musics = new ArrayList<>();
        List<Playlist> playlists = new ArrayList<>();

        // parse best musics
        if (data.has("musics")) {
            JSONArray musicsJsonArray = data.getJSONArray("musics");
            for (int i = 0; i < musicsJsonArray.length(); i++) {
                Music music = JsonParser.musicJsonParser(musicsJsonArray.getJSONObject(i));
                if (music != null)
                    musics.add(music);
            }
        }
//        // parse albums
        if (data.has("albums")) {
            JSONArray albumJsonArray = data.getJSONArray("albums");
            for (int i = 0; i < albumJsonArray.length(); i++) {
                Album album = JsonParser.albumJsonParser(albumJsonArray.getJSONObject(i));
                if (album != null)
                    albums.add(album);
            }
        }
//        // parse artists
        if (data.has("artists")) {
            JSONArray artistsJsonArray = data.getJSONArray("artists");
            for (int i = 0; i < artistsJsonArray.length(); i++) {
                Artist artist = JsonParser.artistJsonArray(artistsJsonArray.getJSONObject(i));
                if (artist != null)
                    artists.add(artist);
            }
        }
//        // parse videos
        if (data.has("videos")) {
            JSONArray videosJsonArray = data.getJSONArray("videos");
            for (int i = 0; i < videosJsonArray.length(); i++) {
                Video video = JsonParser.videoJsonArray(videosJsonArray.getJSONObject(i));
                if (video != null)
                    videos.add(video);
            }
        }
//        // parse playlist
        if (data.has("playlists")) {
            JSONArray playlistsJsonArray = data.getJSONArray("playlists");
            for (int i = 0; i < playlistsJsonArray.length(); i++) {
                Playlist playlist = JsonParser.playlistJsonParser(playlistsJsonArray.getJSONObject(i));
                if (playlist != null)
                    playlists.add(playlist);
            }
        }

        search.setAlbums(albums);
        search.setArtists(artists);
        search.setPlaylists(playlists);
        search.setMusics(musics);
        search.setVideos(videos);

        return search;
    }

}
