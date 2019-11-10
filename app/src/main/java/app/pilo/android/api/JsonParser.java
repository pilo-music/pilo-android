package app.pilo.android.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.HeroSlider;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.User;
import app.pilo.android.models.Video;

class JsonParser {
    static Music musicJsonParser(JSONObject jsonObject) {
        try {
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

    static Album albumJsonParser(JSONObject jsonObject) {
        try {
            List<Music> musics = new ArrayList<>();
            JSONArray musicsJsonArray = jsonObject.getJSONArray("musics");
            for (int i = 0; i < musicsJsonArray.length(); i++) {
                Music music = JsonParser.musicJsonParser(musicsJsonArray.getJSONObject(i));
                if (music != null)
                    musics.add(music);
            }
            JSONObject jsonObjectArtist = jsonObject.getJSONObject("artist");
            return new Album(
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
            Log.e("Albums", "albumJsonParser: " + e.getMessage());
            return null;
        }
    }

    static Artist artistJsonArray(JSONObject jsonObject) {
        try {
            int music_count = 0;
            if (!jsonObject.isNull("music_count"))
                music_count = jsonObject.getInt("music_count");

            int album_count = 0;
            if (!jsonObject.isNull("album_count"))
                album_count = jsonObject.getInt("album_count");

            int video_count = 0;
            if (!jsonObject.isNull("video_count"))
                video_count = jsonObject.getInt("video_count");

            boolean is_follow = false;
            if (!jsonObject.isNull("is_follow"))
                is_follow = jsonObject.getBoolean("is_follow");

            int is_best = 0;
            if (!jsonObject.isNull("isbest"))
                is_best = jsonObject.getInt("isbest");


            return new Artist(
                    jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("image"),
                    is_best,
                    jsonObject.getString("slug"),
                    music_count,
                    album_count,
                    video_count,
                    is_follow
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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

    static Playlist playlistJsonParser(JSONObject jsonObject) {
        try {
            List<Music> musics = new ArrayList<>();
            JSONArray musicsJsonArray = jsonObject.getJSONArray("musics");
            for (int i = 0; i < musicsJsonArray.length(); i++) {
                Music music = JsonParser.musicJsonParser(musicsJsonArray.getJSONObject(i));
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


    static HeroSlider heroSliderJsonParser(JSONObject jsonObject) {
        HeroSlider heroSlider = new HeroSlider();

        return heroSlider;
    }

    static User userJsonParser(JSONObject jsonObject) {
        User user = new User();
        try {
            if (!jsonObject.isNull("access_token")) {
                user.setAccess_token(jsonObject.getString("access_token"));
            }
            if (!jsonObject.isNull("user")) {
                JSONObject userJsonObject = jsonObject.getJSONObject("user");
                if (!userJsonObject.isNull("name"))
                    user.setName(userJsonObject.getString("name"));
                if (!userJsonObject.isNull("email"))
                    user.setEmail(userJsonObject.getString("email"));
                if (!userJsonObject.isNull("phone"))
                    user.setPhone(userJsonObject.getString("phone"));
                if (!userJsonObject.isNull("birth"))
                    user.setPhone(userJsonObject.getString("phone"));
                if (!userJsonObject.isNull("gender"))
                    user.setGender(userJsonObject.getString("gender"));
                if (!userJsonObject.isNull("pic"))
                    user.setPic(userJsonObject.getString("pic"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
