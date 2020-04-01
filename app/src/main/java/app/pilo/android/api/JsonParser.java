package app.pilo.android.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Bookmark;
import app.pilo.android.models.HeroSlider;
import app.pilo.android.models.Like;
import app.pilo.android.models.Message;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.models.User;
import app.pilo.android.models.Video;

class JsonParser {
    static Music singleMusicParser(JSONObject jsonObject) {
        try {
            //todo add duration
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Music(
                    jsonObject.getInt("id"),
                    jsonObject.getString("slug"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("url"),
                    jsonObject.getInt("isbest"),
                    jsonObject.getBoolean("has_like"),
                    jsonObject.getBoolean("has_bookmark"),
                    jsonObjectArtist.getInt("id"),
                    jsonObjectArtist.getString("name"),
                    jsonObjectArtist.getString("slug")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Album albumParser(JSONObject jsonObject) {
        try {
            Artist artist = JsonParser.artistParser(jsonObject.getJSONObject("artist"));
            return new Album(
                    jsonObject.getString("slug"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("thumbnail"),
                    jsonObject.getInt("music_count"),
                    jsonObject.getInt("like_count"),
                    jsonObject.getInt("play_count"),
                    jsonObject.getString("created_at"),
                    artist
            );
        } catch (JSONException e) {
            Log.e("Albums", "albumParser: " + e.getMessage());
            return null;
        }
    }

    static SingleAlbum singleAlbumParser(JSONObject data) throws JSONException {

        SingleAlbum singleAlbum = new SingleAlbum();

        List<Music> musics = new ArrayList<>();
        List<Album> related = new ArrayList<>();

        Album album = JsonParser.albumParser(data.getJSONObject("album"));
        boolean has_like = data.getBoolean("has_like");
        boolean has_bookmark = data.getBoolean("has_bookmark");

        // parse musics
        JSONArray musicsJsonArray = data.getJSONArray("musics");
        for (int i = 0; i < musicsJsonArray.length(); i++) {
            Music music = JsonParser.singleMusicParser(musicsJsonArray.getJSONObject(i));
            if (music != null)
                musics.add(music);
        }

        // parse related
        JSONArray relatedJsonArray = data.getJSONArray("related");
        for (int i = 0; i < relatedJsonArray.length(); i++) {
            Album albumItem = JsonParser.albumParser(musicsJsonArray.getJSONObject(i));
            if (albumItem != null)
                related.add(albumItem);
        }

        singleAlbum.setAlbum(album);
        singleAlbum.setMusics(musics);
        singleAlbum.setRelated(related);
        singleAlbum.setHas_bookmark(has_bookmark);
        singleAlbum.setHas_like(has_like);

        return singleAlbum;
    }

    static Artist artistParser(JSONObject jsonObject) {
        try {
            return new Artist(
                    jsonObject.getString("slug"),
                    jsonObject.getString("name"),
                    jsonObject.getString("image"),
                    jsonObject.getString("thumbnail"),
                    jsonObject.getInt("music_count"),
                    jsonObject.getInt("album_count"),
                    jsonObject.getInt("video_count"),
                    jsonObject.getInt("followers_count"),
                    jsonObject.getInt("playlist_count"),
                    jsonObject.getString("created_at")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static SingleArtist singleArtistParser(JSONObject data) throws JSONException {

        SingleArtist singleArtist = new SingleArtist();
        List<Music> bestMusics = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        List<Video> videos = new ArrayList<>();
        List<Music> lastMusics = new ArrayList<>();

        //parse artist
        Artist artist = JsonParser.artistParser(data.getJSONObject("artist"));

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

        singleArtist.setAlbums(albums);
        singleArtist.setBest_musics(bestMusics);
        singleArtist.setLast_musics(lastMusics);
        singleArtist.setVideos(videos);
        singleArtist.setArtist(artist);

        return singleArtist;
    }

    static Video videoJsonArray(JSONObject jsonObject) {
        try {
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Video(
                    jsonObject.getInt("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("slug"),
                    jsonObject.getInt("isbest"),
                    jsonObject.getString("video480"),
                    jsonObject.getString("video720"),
                    jsonObject.getString("video1080"),
                    jsonObject.getString("url"),
                    jsonObject.getBoolean("has_like"),
                    jsonObject.getBoolean("has_bookmark"),
                    jsonObjectArtist.getInt("id"),
                    jsonObjectArtist.getString("name"),
                    jsonObjectArtist.getString("slug")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Playlist playlistParser(JSONObject jsonObject) {
        try {
            List<Music> musics = new ArrayList<>();
            JSONArray musicsJsonArray = jsonObject.getJSONArray("musics");
            for (int i = 0; i < musicsJsonArray.length(); i++) {
                Music music = JsonParser.singleMusicParser(musicsJsonArray.getJSONObject(i));
                if (music != null)
                    musics.add(music);
            }
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Playlist(
                    jsonObject.getInt("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getInt("isbest"),
                    jsonObject.getString("slug"),
                    jsonObject.getBoolean("has_like"),
                    jsonObjectArtist.getInt("id"),
                    jsonObjectArtist.getString("name"),
                    jsonObjectArtist.getString("slug"),
                    musics
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    static HeroSlider heroSliderParser(JSONObject jsonObject) {
        HeroSlider heroSlider = new HeroSlider();

        return heroSlider;
    }

    static User userParser(JSONObject jsonObject) {
        User user = new User();
        try {
            if (!jsonObject.isNull("access_token")) {
                user.setAccess_token(jsonObject.getString("access_token"));
            }
            if (!jsonObject.isNull("user")) {
                JSONObject userJsonObject = jsonObject.getJSONObject("user");
                user.setName(userJsonObject.getString("name"));
                user.setEmail(userJsonObject.getString("email"));
                user.setPhone(userJsonObject.getString("phone"));
                user.setPhone(userJsonObject.getString("phone"));
                user.setGender(userJsonObject.getString("gender"));
                user.setPic(userJsonObject.getString("pic"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }


    static Bookmark bookmarkParser(JSONObject jsonObject) {
        try {
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Bookmark(
                    jsonObject.getInt("id"),
                    jsonObject.getString("type"),
                    jsonObject.getString("slug"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("created_at"),
                    jsonObjectArtist.getInt("id"),
                    jsonObjectArtist.getString("name"),
                    jsonObjectArtist.getString("slug")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    static Like likeParser(JSONObject jsonObject) {
        try {
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Like(
                    jsonObject.getInt("id"),
                    jsonObject.getString("type"),
                    jsonObject.getString("slug"),
                    jsonObject.getString("title"),
                    jsonObject.getString("image"),
                    jsonObject.getString("created_at"),
                    jsonObjectArtist.getInt("id"),
                    jsonObjectArtist.getString("name"),
                    jsonObjectArtist.getString("slug")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Message messageParser(JSONObject jsonObject) {
        try {
            return new Message(
                    jsonObject.getInt("id"),
                    jsonObject.getInt("sender"),
                    jsonObject.getString("subject"),
                    jsonObject.getString("text"),
                    jsonObject.getString("type"),
                    jsonObject.getString("created_at")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
