package app.pilo.android.models;

public class Artist {
    private int id;
    private String name;
    private String image;
    private int isbest;
    private String slug;

    private int musics_count;
    private int album_count;
    private int video_count;
    private boolean is_follow;

    public Artist() {
        this.id = 0;
        this.name = "";
        this.image = "";
        this.isbest = 0;
        this.slug = "";
        this.musics_count = 0;
        this.album_count = 0;
        this.video_count = 0;
        this.is_follow = false;
    }

    public Artist(int id, String name, String image, int isbest, String slug, int musics_count, int album_count, int video_count, boolean is_follow) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isbest = isbest;
        this.slug = slug;
        this.musics_count = musics_count;
        this.album_count = album_count;
        this.video_count = video_count;
        this.is_follow = is_follow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getMusics_count() {
        return musics_count;
    }

    public void setMusics_count(int musics_count) {
        this.musics_count = musics_count;
    }

    public int getAlbum_count() {
        return album_count;
    }

    public void setAlbum_count(int album_count) {
        this.album_count = album_count;
    }

    public int getVideo_count() {
        return video_count;
    }

    public void setVideo_count(int video_count) {
        this.video_count = video_count;
    }

    public boolean isIs_follow() {
        return is_follow;
    }

    public void setIs_follow(boolean is_follow) {
        this.is_follow = is_follow;
    }
}
