package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "musics")
public class Music {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "music_slug")
    private String slug;
    @ColumnInfo(name = "music_title")
    private String title;
    @ColumnInfo(name = "music_image")
    private String image;
    @ColumnInfo(name = "music_thumbnail")
    private String thumbnail;
    @ColumnInfo(name = "music_link128")
    private String link128;
    @ColumnInfo(name = "music_link320")
    private String link320;
    @ColumnInfo(name = "music_lyric")
    private String lyric;
    @ColumnInfo(name = "music_like_count")
    private int like_count;
    @ColumnInfo(name = "music_play_count")
    private int play_count;
    @ColumnInfo(name = "music_share_url")
    private String share_url;
    @ColumnInfo(name = "music_created_at")
    private String created_at;
    @ColumnInfo(name = "music_has_like")
    private boolean has_like;

    @Embedded
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
        this.share_url = "";
        this.created_at = "";
        this.artist = new Artist();
        this.tags = new ArrayList<>();
        this.setHas_like(false);
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

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }
}
