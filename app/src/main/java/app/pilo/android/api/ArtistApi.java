package app.pilo.android.api;

import com.loopj.android.http.JsonHttpResponseHandler;

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
import cz.msebera.android.httpclient.Header;

public class ArtistApi {

    public void get(String type, int page, final RequestHandler.RequestHandlerWithList<Artist> requestHandler) {
        PiloApi.get(PiloApi.ARTISTS_GET + page + "/" + type, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    String status = response.getString("status");
                    List<Artist> artists = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        Artist artist = JsonParser.artistJsonArray(data.getJSONObject(i));
                        if (artist != null)
                            artists.add(artist);
                    }
                    requestHandler.onGetInfo(status, artists);
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


    public void single(String slug, final RequestHandler.RequestHandlerWithModel<SingleArtist> requestHandler) {
        PiloApi.get(PiloApi.ARTIST_GET + slug, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String status = response.getString("status");
                    SingleArtist singleArtist = parsArtistApiData(data);

                    if (singleArtist != null)
                        requestHandler.onGetInfo(status, singleArtist);
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
