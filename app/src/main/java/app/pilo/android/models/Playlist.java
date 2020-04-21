package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String slug;
    private String title;
    private String image;
    private String image_one;
    private String image_two;
    private String image_three;
    private String image_four;
    private int music_count;
    private int like_count;
    private int play_count;
    private String share_url;
    private String created_at;

    public Playlist(){
        this.slug = "";
        this.title = "";
        this.image = "";
        this.image_one = "";
        this.image_two = "";
        this.image_three = "";
        this.image_four = "";
        this.music_count = 0;
        this.like_count = 0;
        this.play_count = 0;
        this.share_url = "";
        this.created_at = "";
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

    public String getImage_one() {
        return image_one;
    }

    public void setImage_one(String image_one) {
        this.image_one = image_one;
    }

    public String getImage_two() {
        return image_two;
    }

    public void setImage_two(String image_two) {
        this.image_two = image_two;
    }

    public String getImage_three() {
        return image_three;
    }

    public void setImage_three(String image_three) {
        this.image_three = image_three;
    }

    public String getImage_four() {
        return image_four;
    }

    public void setImage_four(String image_four) {
        this.image_four = image_four;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }
}
