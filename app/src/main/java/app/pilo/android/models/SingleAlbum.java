package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class SingleAlbum {

    private Album album;
    private List<Music> musics;
    private List<Album> related;
    private boolean has_like;
    private boolean has_bookmark;


    public SingleAlbum() {
        this.album = new Album();
        this.musics = new ArrayList<>();
        this.related = new ArrayList<>();
        this.has_like = false;
        this.has_bookmark = false;
    }


    public SingleAlbum(Album album, List<Music> musics, List<Album> related, boolean has_like, boolean has_bookmark) {
        this.album = album;
        this.musics = musics;
        this.related = related;
        this.has_like = has_like;
        this.has_bookmark = has_bookmark;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public List<Album> getRelated() {
        return related;
    }

    public void setRelated(List<Album> related) {
        this.related = related;
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
