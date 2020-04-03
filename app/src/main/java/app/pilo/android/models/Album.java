package app.pilo.android.models;

public class Album {

    private String slug;
    private String title;
    private String image;
    private String thumbnail;
    private int music_count;
    private int like_count;
    private int play_count;
    private String created_at;
    private Artist artist;

    public Album() {
        this.slug = "";
        this.title = "";
        this.image = "";
        this.thumbnail = "";
        this.music_count = 0;
        this.like_count = 0;
        this.play_count = 0;
        this.created_at = "";
        this.artist = new Artist();
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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
