package app.pilo.android.models;

public class Music {
    private int id;
    private String slug;
    private String title;
    private String image;
    private String url;
    private int isbest;
    private int has_like;
    private int has_bookmark;
    private int artist_id;
    private String artist_name;
    private String artist_slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsbest() {
        return isbest;
    }

    public void setIsbest(int isbest) {
        this.isbest = isbest;
    }

    public int getHas_like() {
        return has_like;
    }

    public void setHas_like(int has_like) {
        this.has_like = has_like;
    }

    public int getHas_bookmark() {
        return has_bookmark;
    }

    public void setHas_bookmark(int has_bookmark) {
        this.has_bookmark = has_bookmark;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_slug() {
        return artist_slug;
    }

    public void setArtist_slug(String artist_slug) {
        this.artist_slug = artist_slug;
    }
}
