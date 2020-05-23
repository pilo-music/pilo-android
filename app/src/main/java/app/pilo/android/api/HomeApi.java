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
import app.pilo.android.models.ForYou;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Video;
import app.pilo.android.repositories.UserRepo;

public class HomeApi {
    private Context context;

    public HomeApi(Context context) {
        this.context = context;
    }

    public void getBrowse(final HttpHandler.RequestHandler requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.BROWSER_GET, null,
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
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public void singleBrowse(int id, int page, final HttpHandler.RequestHandler requestHandler) {
        StringBuilder url = new StringBuilder(PiloApi.BROWSER_SINGLE);
        url.append("?").append("id").append("=").append(id);
        url.append("&").append("page").append("=").append(page);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            requestHandler.onGetInfo(parsSingleHomeApiData(data), message, status);
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


    public void getHome(final HttpHandler.RequestHandler requestHandler) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PiloApi.HOME_GET, null,
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
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(18000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    public void singleHome(int id, int page, final HttpHandler.RequestHandler requestHandler) {
        StringBuilder url = new StringBuilder(PiloApi.HOME_SINGLE);
        url.append("?").append("id").append("=").append(id);
        url.append("&").append("page").append("=").append(page);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        boolean status = response.getBoolean("status");
                        String message = response.getString("message");
                        if (status) {
                            requestHandler.onGetInfo(parsSingleHomeApiData(data), message, status);
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


    private List<Home> parsHomeApiData(JSONArray jsonArray) throws JSONException {
        List<Home> homes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Home home = new Home();
            Object data;
            switch (jsonArray.getJSONObject(i).getString("type")) {
                case Home.TYPE_ARTISTS:
                    List<Artist> artists = new ArrayList<>();
                    JSONArray artistsData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (artistsData.length() > 0) {
                        for (int j = 0; j < artistsData.length(); j++) {
                            Artist artist = JsonParser.artistParser(artistsData.getJSONObject(j));
                            if (artist != null)
                                artists.add(artist);
                        }
                    }
                    data = artists;
                    break;
                case Home.TYPE_MUSICS:
                case Home.TYPE_MUSIC_GRID:
                case Home.TYPE_MUSIC_VERTICAL:
                case Home.TYPE_TRENDING:
                    List<Music> musics = new ArrayList<>();
                    JSONArray musicsData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (musicsData.length() > 0) {
                        for (int j = 0; j < musicsData.length(); j++) {
                            Music music = JsonParser.musicParser(musicsData.getJSONObject(j));
                            if (music != null)
                                musics.add(music);
                        }
                    }
                    data = musics;
                    break;
                case Home.TYPE_ALBUMS:
                    List<Album> albums = new ArrayList<>();
                    JSONArray albumsData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (albumsData.length() > 0) {
                        for (int j = 0; j < albumsData.length(); j++) {
                            Album album = JsonParser.albumParser(albumsData.getJSONObject(j));
                            if (album != null)
                                albums.add(album);
                        }
                    }
                    data = albums;
                    break;
                case Home.TYPE_PLAYLISTS:
                case Home.TYPE_PLAYLIST_GRID:
                    List<Playlist> playlists = new ArrayList<>();
                    JSONArray playlistsData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (playlistsData.length() > 0) {
                        for (int j = 0; j < playlistsData.length(); j++) {
                            Playlist playlist = JsonParser.playlistParser(playlistsData.getJSONObject(j));
                            if (playlist != null)
                                playlists.add(playlist);
                        }
                    }
                    data = playlists;
                    break;
                case Home.TYPE_PROMOTION:
                    JSONObject promotionsData = jsonArray.getJSONObject(i);
                    data = JsonParser.promotionParser(promotionsData.getJSONObject("data"));
                    break;
                case Home.TYPE_ALBUM_MUSIC_GRID:
                    List<Object> albumMusics = new ArrayList<>();
                    JSONArray albumMusicsData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (albumMusicsData.length() > 0) {
                        for (int j = 0; j < albumMusicsData.length(); j++) {
                            String type = albumMusicsData.getJSONObject(i).getString("type");
                            if (type.equals("music")) {
                                Music music = JsonParser.musicParser(albumMusicsData.getJSONObject(j));
                                if (music != null)
                                    albumMusics.add(music);
                            } else {
                                Album album = JsonParser.albumParser(albumMusicsData.getJSONObject(j));
                                if (album != null)
                                    albumMusics.add(album);
                            }
                        }
                    }
                    data = albumMusics;
                    break;
                case Home.TYPE_VIDEOS:
                    List<Video> videos = new ArrayList<>();
                    JSONArray videoData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (videoData.length() > 0) {
                        for (int j = 0; j < videoData.length(); j++) {
                            Video video = JsonParser.videoJson(videoData.getJSONObject(j));
                            if (video != null)
                                videos.add(video);
                        }
                    }
                    data = videos;
                    break;
                case Home.TYPE_BROWSE_DOCK:
                case Home.TYPE_MUSIC_FOLLOWS:
                case Home.TYPE_PLAY_HISTORY:
                    data = new Object();
                    break;
                case Home.TYPE_FOR_YOU:
                    List<ForYou> forYous = new ArrayList<>();
                    JSONArray forYousData = jsonArray.getJSONObject(i).getJSONArray("data");
                    if (forYousData.length() > 0) {
                        for (int j = 0; j < forYousData.length(); j++) {
                            ForYou forYou = JsonParser.forYouParser(forYousData.getJSONObject(j));
                            if (forYou != null)
                                forYous.add(forYou);
                        }
                    }
                    data = forYous;
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


    public Home parsSingleHomeApiData(JSONObject jsonObject) throws JSONException {
        Home home = new Home();
        Object data;
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        switch (jsonObject.getString("type")) {
            case Home.TYPE_ARTISTS:
                List<Artist> artists = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Artist artist = JsonParser.artistParser(jsonArray.getJSONObject(j));
                        if (artist != null)
                            artists.add(artist);
                    }
                }
                data = artists;
                break;
            case Home.TYPE_MUSICS:
            case Home.TYPE_MUSIC_GRID:
            case Home.TYPE_MUSIC_VERTICAL:
            case Home.TYPE_TRENDING:
                List<Music> musics = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Music music = JsonParser.musicParser(jsonArray.getJSONObject(j));
                        if (music != null)
                            musics.add(music);
                    }
                }
                data = musics;
                break;
            case Home.TYPE_ALBUMS:
                List<Album> albums = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Album album = JsonParser.albumParser(jsonArray.getJSONObject(j));
                        if (album != null)
                            albums.add(album);
                    }
                }
                data = albums;
                break;
            case Home.TYPE_PLAYLISTS:
            case Home.TYPE_PLAYLIST_GRID:
                List<Playlist> playlists = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Playlist playlist = JsonParser.playlistParser(jsonArray.getJSONObject(j));
                        if (playlist != null)
                            playlists.add(playlist);
                    }
                }
                data = playlists;
                break;
            case Home.TYPE_ALBUM_MUSIC_GRID:
                List<Object> albumMusics = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        String type = jsonArray.getJSONObject(j).getString("type");
                        if (type.equals("music")) {
                            Music music = JsonParser.musicParser(jsonArray.getJSONObject(j));
                            if (music != null)
                                albumMusics.add(music);
                        } else {
                            Album album = JsonParser.albumParser(jsonArray.getJSONObject(j));
                            if (album != null)
                                albumMusics.add(album);
                        }
                    }
                }
                data = albumMusics;
                break;
            case Home.TYPE_VIDEOS:
                List<Video> videos = new ArrayList<>();
                if (jsonArray.length() > 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Video video = JsonParser.videoJson(jsonArray.getJSONObject(j));
                        if (video != null)
                            videos.add(video);
                    }
                }
                data = videos;
                break;
            case Home.TYPE_BROWSE_DOCK:
            case Home.TYPE_MUSIC_FOLLOWS:
            case Home.TYPE_FOR_YOU:
            case Home.TYPE_PLAY_HISTORY:
                data = new Object();
                break;
            default:
                data = null;
                break;
        }
        if (data != null) {
            home.setData(data);
            home.setType(jsonObject.getString("type"));
            home.setId(jsonObject.getInt("id"));
            home.setName(jsonObject.getString("name"));
        }
        return home;
    }
}
