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
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.models.Video;

public class ArtistApi {

    private Context context;

    public ArtistApi(Context context) {
        this.context = context;
    }


    public void get(int page, final RequestHandler.RequestHandlerWithList<Artist> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTISTS_GET + page , null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            List<Artist> artists = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                Artist artist = JsonParser.artistJsonArray(data.getJSONObject(i));
                                if (artist != null)
                                    artists.add(artist);
                            }
                            requestHandler.onGetInfo(status, artists);
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

    public void single(String slug, final RequestHandler.RequestHandlerWithModel<SingleArtist> requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.ARTIST_GET + slug, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            SingleArtist singleArtist = parsArtistApiData(data);
                            if (singleArtist != null)
                                requestHandler.onGetInfo(status, singleArtist);
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

    private SingleArtist parsArtistApiData(JSONObject data) throws JSONException {

        SingleArtist singleArtist = new SingleArtist();
        List<Music> bestMusics = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        List<Video> videos = new ArrayList<>();
        List<Music> lastMusics = new ArrayList<>();

        //parse artist
        Artist artist = JsonParser.artistJsonArray(data.getJSONObject("artist"));

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

        singleArtist.setAlbums(albums);
        singleArtist.setBest_musics(bestMusics);
        singleArtist.setLast_musics(lastMusics);
        singleArtist.setVideos(videos);
        singleArtist.setArtist(artist);

        return singleArtist;
    }


}
