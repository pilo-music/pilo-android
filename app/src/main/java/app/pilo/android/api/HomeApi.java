package app.pilo.android.api;

import org.json.*;

import com.loopj.android.http.*;

import java.util.ArrayList;
import java.util.List;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;
import cz.msebera.android.httpclient.Header;

public class HomeApi {

    public void get(final RequestHandler.RequestHandlerWithModel<Home> requestHandler) {
        PiloApi.get(PiloApi.HOME_GET, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String status = response.getString("status");
                    Home home = parsHomeApiData(data);
                    if (home != null)
                        requestHandler.onGetInfo(status, home);
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

    private Home parsHomeApiData(JSONObject data) throws JSONException {
        Home home = new Home();
        List<Music> bestMusics = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        List<Video> videos = new ArrayList<>();
        List<Music> lastMusics = new ArrayList<>();
//        List<HeroSlider> heroSliders = new ArrayList<>();

        //parse hero slider

//        JSONArray heroSliderJsonArray = data.getJSONArray("hero_slider");
//        for (int i = 0; i < heroSliderJsonArray.length(); i++) {
//            HeroSlider heroSlider = JsonParser.heroSliderJsonParser(heroSliderJsonArray.getJSONObject(i));
//            if (heroSlider != null)
//                heroSliders.add(heroSlider);
//        }

        // parse best musics
        JSONArray bestMusicJsonArray = data.getJSONArray("best_musics");
        for (int i = 0; i < bestMusicJsonArray.length(); i++) {
            Music music = JsonParser.musicJsonParser(bestMusicJsonArray.getJSONObject(i));
            if (music != null)
                bestMusics.add(music);
        }
//        // parse albums
        JSONArray albumJsonArray = data.getJSONArray("albums");
        for (int i = 0; i < albumJsonArray.length(); i++) {
            Album album = JsonParser.albumJsonParser(albumJsonArray.getJSONObject(i));
            if (album != null)
                albums.add(album);
        }
//        // parse artists
        JSONArray artistsJsonArray = data.getJSONArray("artists");
        for (int i = 0; i < albumJsonArray.length(); i++) {
            Artist artist = JsonParser.artistJsonArray(artistsJsonArray.getJSONObject(i));
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
            Music music = JsonParser.musicJsonParser(lastMusicJsonArray.getJSONObject(i));
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
