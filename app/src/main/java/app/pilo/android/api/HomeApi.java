package app.pilo.android.api;

import android.content.Context;

import org.json.*;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Promotion;
import app.pilo.android.models.Video;
import app.pilo.android.repositories.UserRepo;

public class HomeApi {
    private Context context;

    public HomeApi(Context context) {
        this.context = context;
    }

    public void get(final HttpHandler.RequestHandler requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ALBUM_GET, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            requestHandler.onGetInfo(parsHomeApiData(data), message, status);
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
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    private List<Home> parsHomeApiData(JSONArray jsonArray) throws JSONException {
        List<Home> homes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Home home = new Home();
            Object data = new Object();
            switch (jsonArray.getJSONObject(i).getString("type")) {
                case "artists":
                    List<Artist> artists = new ArrayList<>();
                    JSONArray artistsData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < artistsData.length(); j++) {
                        Artist artist = JsonParser.artistParser(artistsData.getJSONObject(i));
                        if (artist != null)
                            artists.add(artist);
                    }
                    data = artists;
                    break;
                case "musics":
                    List<Music> musics = new ArrayList<>();
                    JSONArray musicsData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < musicsData.length(); j++) {
                        Music music = JsonParser.musicParser(musicsData.getJSONObject(i));
                        if (music != null)
                            musics.add(music);
                    }
                    data = musics;
                    break;
                case "albums":
                    List<Album> albums = new ArrayList<>();
                    JSONArray albumsData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < albumsData.length(); j++) {
                        Album album = JsonParser.albumParser(albumsData.getJSONObject(i));
                        if (album != null)
                            albums.add(album);
                    }
                    data = albums;
                    break;
                case "playlists":
                    List<Playlist> playlists = new ArrayList<>();
                    JSONArray playlistsData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < playlistsData.length(); j++) {
                        Playlist playlist = JsonParser.playlistParser(playlistsData.getJSONObject(i));
                        if (playlist != null)
                            playlists.add(playlist);
                    }
                    data = playlists;
                    break;
                case "promotion":
                    List<Promotion> promotions = new ArrayList<>();
                    JSONArray promotionsData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < promotionsData.length(); j++) {
                        Promotion promotion = JsonParser.promotionParser(promotionsData.getJSONObject(i));
                        if (promotion != null)
                            promotions.add(promotion);
                    }
                    data = promotions;
                    break;
                case "album_music_grid":
                    break;
                case "music_grid":
                    List<Music> musicGrids = new ArrayList<>();
                    JSONArray musicGridData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < musicGridData.length(); j++) {
                        Music music = JsonParser.musicParser(musicGridData.getJSONObject(i));
                        if (music != null)
                            musicGrids.add(music);
                    }
                    data = musicGrids;
                    break;
                case "playlist_grid":
                    List<Playlist> playlistGrids = new ArrayList<>();
                    JSONArray playlistGrid = jsonArray.getJSONArray(i);
                    for (int j = 0; j < playlistGrid.length(); j++) {
                        Playlist playlist = JsonParser.playlistParser(playlistGrid.getJSONObject(i));
                        if (playlist != null)
                            playlistGrids.add(playlist);
                    }
                    data = playlistGrids;
                    break;
                case "trending":
                    List<Music> trending = new ArrayList<>();
                    JSONArray trendingData = jsonArray.getJSONArray(i);
                    for (int j = 0; j < trendingData.length(); j++) {
                        Music music = JsonParser.musicParser(trendingData.getJSONObject(i));
                        if (music != null)
                            trending.add(music);
                    }
                    data = trending;
                    break;
                default:
                    data = null;
                    break;
            }
            if (data != null) {
                home.setData(data);
                home.setType(jsonArray.getJSONObject(i).getString("type"));
                home.setId(jsonArray.getJSONObject(i).getInt("id"));
                home.setName(jsonArray.getJSONObject(i).getString("name"));
                homes.add(home);
            }
        }
        return homes;
    }
}
