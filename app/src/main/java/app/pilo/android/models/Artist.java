package app.pilo.android.models;

public class Artist {
    private int id;
    private String name;
    private String image;
    private int isbest;
    private String slug;

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
