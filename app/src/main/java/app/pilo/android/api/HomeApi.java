package app.pilo.android.api;

import android.content.Context;

import org.json.*;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;

public class HomeApi {
    private Context context;

    public HomeApi(Context context) {
        this.context = context;
    }

    public void get(final RequestHandler.RequestHandlerWithModel<Home> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.HOME_GET, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Home home = parsHomeApiData(data);
                            if (home != null)
                                requestHandler.onGetInfo(status, home);
                            else
                                requestHandler.onGetError(null);
                        } else {
                            requestHandler.onGetInfo(status, null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestHandler.onGetError(null);
                    }
                }, requestHandler::onGetError);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
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
            Music music = JsonParser.singleMusicParser(bestMusicJsonArray.getJSONObject(i));
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
            Video video = JsonParser.videoJsonArray(videosJsonArray.getJSONObject(i));
            if (video != null)
                videos.add(video);
        }
//        // parse last musics
        JSONArray lastMusicJsonArray = data.getJSONArray("last_musics");
        for (int i = 0; i < lastMusicJsonArray.length(); i++) {
            Music music = JsonParser.singleMusicParser(lastMusicJsonArray.getJSONObject(i));
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
