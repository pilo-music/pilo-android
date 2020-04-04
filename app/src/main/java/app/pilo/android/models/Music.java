package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "musics")
public class Music {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "slug")
    private String slug;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "image")
    private String image;
    @ColumnInfo(name = "thumbnail")
    private String thumbnail;
    @ColumnInfo(name = "link128")
    private String link128;
    @ColumnInfo(name = "link320")
    private String link320;
    @ColumnInfo(name = "lyric")
    private String lyric;
    @ColumnInfo(name = "like_count")
    private int like_count;
    @ColumnInfo(name = "play_count")
    private int play_count;
    @ColumnInfo(name = "created_at")
    private String created_at;

    @Ignore
    private Artist artist;
    @Ignore
    private List<Artist> tags;


    public Music() {
        this.slug = "";
        this.title = "";
        this.image = "";
        this.thumbnail = "";
        this.link128 = "";
        this.link320 = "";
        this.lyric = "";
        this.like_count = 0;
        this.play_count = 0;
        this.created_at = "";
        this.artist = new Artist();
        this.tags = new ArrayList<>();
    }




    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink128() {
        return link128;
    }

    public void setLink128(String link128) {
        this.link128 = link128;
    }

    public String getLink320() {
        return link320;
    }

    public void setLink320(String link320) {
        this.link320 = link320;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getPlay_count() {
        return play_count;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<Artist> getTags() {
        return tags;
    }

    public void setTags(List<Artist> tags) {
        this.tags = tags;
    }

}
