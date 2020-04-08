package app.pilo.android.models;


import java.util.ArrayList;
import java.util.List;

public class SingleVideo {

    private Video video;
    private List<Video> related;
    private boolean has_like;
    private boolean has_bookmark;

    public SingleVideo() {
        this.video = new Video();
        this.related = new ArrayList<>();
        this.has_like = false;
        this.has_bookmark = false;
    }


    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public List<Video> getRelated() {
        return related;
    }

    public void setRelated(List<Video> related) {
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
