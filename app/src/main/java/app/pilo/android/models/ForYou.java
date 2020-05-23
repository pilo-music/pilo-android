package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class ForYou {

    private String title;
    private String image;
    private int music_count;
    private List<Music> musics;


    public ForYou(){
        this.title = "";
        this.image = "";
        this.music_count = 0;
        this.musics = new ArrayList<>();
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

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }
}
