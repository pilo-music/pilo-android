package app.pilo.android.models;

public class Artist {
    private String slug;
    private String name;
    private String image;
    private String thumbnail;
    private int musics_count;
    private int album_count;
    private int video_count;
    private int playlist_count;
    private int followers_count;
    private String share_url;
    private String created_at;

    public Artist() {
        this.slug = "";
        this.name = "";
        this.image = "";
        this.thumbnail = "";
        this.musics_count = 0;
        this.album_count = 0;
        this.video_count = 0;
        this.playlist_count = 0;
        this.followers_count = 0;
        this.share_url = "";
        this.created_at = "";
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getPlaylist_count() {
        return playlist_count;
    }

    public void setPlaylist_count(int playlist_count) {
        this.playlist_count = playlist_count;
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
