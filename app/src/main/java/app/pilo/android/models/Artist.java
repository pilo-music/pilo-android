package app.pilo.android.models;

public class Artist {
    private int id;
    private String name;
    private String image;
    private int isbest;
    private String slug;

    public Artist() {
        this.id = 0;
        this.name = "";
        this.image = "";
        this.isbest = 0;
        this.slug = "";
    }

    public Artist(int id, String name, String image, int isbest, String slug) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isbest = isbest;
        this.slug = slug;
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
}
