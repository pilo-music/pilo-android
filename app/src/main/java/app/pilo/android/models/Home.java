package app.pilo.android.models;

public class Home {
    public final static String TYPE_ARTISTS = "artists";
    public final static String TYPE_MUSICS = "musics";
    public final static String TYPE_ALBUMS = "albums";
    public final static String TYPE_PLAYLISTS = "playlists";
    public final static String TYPE_PROMOTION = "promotion";
    public final static String TYPE_ALBUM_MUSIC_GRID = "album_music_grid";
    public final static String TYPE_MUSIC_GRID = "music_grid";
    public final static String TYPE_PLAYLIST_GRID = "playlist_grid";
    public final static String TYPE_TRENDING = "trending";
    public final static String TYPE_VIDEOS = "videos";
    public final static String TYPE_MUSIC_VERTICAL = "music_vertical";
    public final static String TYPE_FOR_YOU = "for_you";
    public final static String TYPE_PLAY_HISTORY = "play_history";
    public final static String TYPE_MUSIC_FOLLOWS = "music_follows";
    public final static String TYPE_BROWSE_DOCK = "browse_dock";

    private int id;
    private String type;
    private String name;
    private Object data;

    public Home() {
        this.id = 0;
        this.type = "";
        this.name = "";
        this.data = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}