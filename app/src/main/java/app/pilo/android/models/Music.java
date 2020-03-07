package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "musics")
public class Music {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "slug")
    private String slug;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "image")
    private String image;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "is_best")
    private int isbest;
    @ColumnInfo(name = "has_like")
    private boolean has_like;
    @ColumnInfo(name = "has_bookmark")
    private boolean has_bookmark;
    @ColumnInfo(name = "artist_id")
    private int artist_id;
    @ColumnInfo(name = "artist_name")
    private String artist_name;
    @ColumnInfo(name = "artist_slug")
    private String artist_slug;

    public Music() {
        this.id = 0;
        this.slug = "";
        this.title = "";
        this.image = "";
        this.url = "";
        this.isbest = 0;
        this.has_like = false;
        this.has_bookmark = false;
        this.artist_id = 0;
        this.artist_name = "";
        this.artist_slug = "";
    }


    public Music(int id, String slug, String title, String image, String url, int isbest, boolean has_like, boolean has_bookmark, int artist_id, String artist_name, String artist_slug) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.image = image;
        this.url = url;
        this.isbest = isbest;
        this.has_like = has_like;
        this.has_bookmark = has_bookmark;
        this.artist_id = artist_id;
        this.artist_name = artist_name;
        this.artist_slug = artist_slug;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsbest() {
        return isbest;
    }

    public void setIsbest(int isbest) {
        this.isbest = isbest;
    }

    public boolean getHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public boolean getHas_bookmark() {
        return has_bookmark;
    }

    public void setHas_bookmark(boolean has_bookmark) {
        this.has_bookmark = has_bookmark;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
