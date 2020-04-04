package app.pilo.android.models;


import java.util.ArrayList;
import java.util.List;

public class SingleMusic {

    private Music music;
    private List<Music> related;
    private boolean has_like;
    private boolean has_bookmark;

    public SingleMusic() {
        this.music = new Music();
        this.related = new ArrayList<>();
        this.has_like = false;
        this.has_bookmark = false;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public List<Music> getRelated() {
        return related;
    }

    public void setRelated(List<Music> related) {
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
