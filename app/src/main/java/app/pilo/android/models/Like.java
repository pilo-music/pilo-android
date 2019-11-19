package app.pilo.android.models;

public class Like {
    private int id;
    private String type;
    private String slug;
    private String title;
    private String image;
    private String created_at;
    private int artist_id;
    private String artist_name;
    private String artist_slug;


    public Like() {
        this.id = 0;
        this.type = "";
        this.slug = "";
        this.title = "";
        this.image = "";
        this.created_at = "";
        this.artist_id = 0;
        this.artist_name = "";
        this.artist_slug = "";
    }

    public Like(int id, String type, String slug, String title, String image, String created_at, int artist_id, String artist_name, String artist_slug) {
        this.id = id;
        this.type = type;
        this.slug = slug;
        this.title = title;
        this.image = image;
        this.created_at = created_at;
        this.artist_id = artist_id;
        this.artist_name = artist_name;
        this.artist_slug = artist_slug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
