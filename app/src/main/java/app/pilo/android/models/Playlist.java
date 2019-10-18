package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int id;
    private String title;
    private String image;
    private int isbest;
    private String slug;
    private boolean has_like;
    private int artist_id;
    private String artist_name;
    private String artist_slug;
    private List<Music> musics;

    public Playlist() {
        this.id = 0;
        this.title = "";
        this.image = "";
        this.isbest = 0;
        this.slug = "";
        this.has_like = false;
        this.artist_id = 0;
        this.artist_name = "";
        this.artist_slug = "";
        this.musics = new ArrayList<>();
    }

    public Playlist(int id, String title, String image, int isbest, String slug, boolean has_like, int artist_id, String artist_name, String artist_slug, List<Music> musics) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.isbest = isbest;
        this.slug = slug;
        this.has_like = has_like;
        this.artist_id = artist_id;
        this.artist_name = artist_name;
        this.artist_slug = artist_slug;
        this.musics = musics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIsbest() {
        return isbest;
    }

    public void setIsbest(int isbest) {
        this.isbest = isbest;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_slug() {
        return artist_slug;
    }

    public void setArtist_slug(String artist_slug) {
        this.artist_slug = artist_slug;
    }

    public boolean getHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }
}
