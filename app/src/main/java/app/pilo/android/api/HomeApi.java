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
                        JSONObject data = response.getJSONObject("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            Home home = parsHomeApiData(data);
                            requestHandler.onGetInfo(home, message, status);
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
    }

    private Home parsHomeApiData(JSONObject data) throws JSONException {
        Home home = new Home();
        List<Music> bestMusics = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        List<Video> videos = new ArrayList<>();
        List<Music> lastMusics = new ArrayList<>();
//        List<HeroSlider> heroSliders = new ArrayList<>();

        //parse hero slider

//        JSONArray heroSliderParser = data.getJSONArray("hero_slider");
//        for (int i = 0; i < heroSliderParser.length(); i++) {
//            HeroSlider heroSlider = JsonParser.heroSliderParser(heroSliderParser.getJSONObject(i));
//            if (heroSlider != null)
//                heroSliders.add(heroSlider);
//        }

        // parse best musics
        JSONArray bestMusicJsonArray = data.getJSONArray("best_musics");
        for (int i = 0; i < bestMusicJsonArray.length(); i++) {
            Music music = JsonParser.musicParser(bestMusicJsonArray.getJSONObject(i));
            if (music != null)
                bestMusics.add(music);
        }
//        // parse albums
        JSONArray albumJsonArray = data.getJSONArray("albums");
        for (int i = 0; i < albumJsonArray.length(); i++) {
            Album album = JsonParser.albumParser(albumJsonArray.getJSONObject(i));
            if (album != null)
                albums.add(album);
        }
//        // parse artists
        JSONArray artistsJsonArray = data.getJSONArray("artists");
        for (int i = 0; i < albumJsonArray.length(); i++) {
            Artist artist = JsonParser.artistParser(artistsJsonArray.getJSONObject(i));
            if (artist != null)
                artists.add(artist);
        }
//        // parse videos
        JSONArray videosJsonArray = data.getJSONArray("videos");
        for (int i = 0; i < albumJsonArray.length(); i++) {
            Video video = JsonParser.videoJson(videosJsonArray.getJSONObject(i));
            if (video != null)
                videos.add(video);
        }
//        // parse last musics
        JSONArray lastMusicJsonArray = data.getJSONArray("last_musics");
        for (int i = 0; i < lastMusicJsonArray.length(); i++) {
            Music music = JsonParser.musicParser(lastMusicJsonArray.getJSONObject(i));
            if (music != null)
                lastMusics.add(music);
        }

        home.setAlbums(albums);
        home.setArtists(artists);
        home.setBest_musics(bestMusics);
        home.setLast_music(lastMusics);
//        home.setHero_sliders(heroSliders);
        home.setVideos(videos);

        return home;
    }
}
