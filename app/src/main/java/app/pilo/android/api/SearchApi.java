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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Search;
import app.pilo.android.models.Video;
import app.pilo.android.repositories.UserRepo;

public class SearchApi {
    private Context context;

    public SearchApi(Context context) {
        this.context = context;
    }

    public void get(HashMap<String, Object> params, final HttpHandler.RequestHandler requestHandler) {
        StringBuilder url = new StringBuilder(PiloApi.SEARCH);
        if (params != null) {
            int index = 0;
            for (Map.Entry<String, Object> item : params.entrySet()) {
                if (index != 0) {
                    url.append("&").append(item.getKey()).append("=").append(item.getValue());
                } else {
                    url.append("?").append(item.getKey()).append("=").append(item.getValue());
                }
                index++;
            }
        }
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            Search search = parsSearchApiData(data);
                            requestHandler.onGetInfo(search, message, status);
                        } else
                            requestHandler.onGetInfo(null, message, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Content-Language", new UserSharedPrefManager(context).getLocal());
                params.put("Authorization", "Bearer " + UserRepo.getInstance(context).get().getAccess_token());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
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
            if (musicsJsonArray.length() > 0) {
                for (int i = 0; i < musicsJsonArray.length(); i++) {
                    Music music = JsonParser.musicParser(musicsJsonArray.getJSONObject(i));
                    if (music != null)
                        musics.add(music);
                }
            }
        }
//        // parse albums
        if (data.has("albums")) {
            JSONArray albumJsonArray = data.getJSONArray("albums");
            if (albumJsonArray.length() > 0) {
                for (int i = 0; i < albumJsonArray.length(); i++) {
                    Album album = JsonParser.albumParser(albumJsonArray.getJSONObject(i));
                    if (album != null)
                        albums.add(album);
                }
            }
        }
//        // parse artists
        if (data.has("artists")) {
            JSONArray artistsJsonArray = data.getJSONArray("artists");
            if (artistsJsonArray.length() > 0) {
                for (int i = 0; i < artistsJsonArray.length(); i++) {
                    Artist artist = JsonParser.artistParser(artistsJsonArray.getJSONObject(i));
                    if (artist != null)
                        artists.add(artist);
                }
            }
        }
//        // parse videos
        if (data.has("videos")) {
            JSONArray videosJsonArray = data.getJSONArray("videos");
            if (videosJsonArray.length() > 0) {
                for (int i = 0; i < videosJsonArray.length(); i++) {
                    Video video = JsonParser.videoJson(videosJsonArray.getJSONObject(i));
                    if (video != null)
                        videos.add(video);
                }
            }
        }
//        // parse playlist
        if (data.has("playlists")) {
            JSONArray playlistsJsonArray = data.getJSONArray("playlists");
            if (playlistsJsonArray.length() > 0) {
                for (int i = 0; i < playlistsJsonArray.length(); i++) {
                    Playlist playlist = JsonParser.playlistParser(playlistsJsonArray.getJSONObject(i));
                    if (playlist != null)
                        playlists.add(playlist);
                }
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
