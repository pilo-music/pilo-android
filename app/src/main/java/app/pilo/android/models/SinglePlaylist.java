package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class SinglePlaylist {
    private Playlist playlist;
    private List<Music> musics;
    private boolean has_like;
    private boolean has_bookmark;

    public SinglePlaylist() {
        this.playlist = new Playlist();
        this.musics = new ArrayList<>();
        this.has_like = false;
        this.has_bookmark = false;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public boolean isHas_bookmark() {
        return has_bookmark;
    }

    public void setHas_bookmark(boolean has_bookmark) {
        this.has_bookmark = has_bookmark;
    }
}
