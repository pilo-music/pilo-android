package app.pilo.android.models;

public class Promotion {
    private String title;
    private String image;
    private String slug;
    private String url;
    private String type;

    public Promotion(){
        this.title = "";
        this.image = "";
        this.slug = "";
        this.url = "";
        this.type = "";
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
