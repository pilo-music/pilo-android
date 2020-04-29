package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class ForYou {

    private String slug;
    private String title;
    private String image;
    private int music_count;
    private int like_count;
    private int play_count;
    private String share_url;
    private boolean has_like;
    private List<Music> musics;


    public ForYou(){
        this.slug = "";
        this.title = "";
        this.image = "";
        this.music_count = 0;
        this.like_count = 0;
        this.play_count = 0;
        this.share_url = "";
        this.has_like = false;
        this.musics = new ArrayList<>();
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

    public int getMusic_count() {
        return music_count;
    }

    public void setMusic_count(int music_count) {
        this.music_count = music_count;
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

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }
}
