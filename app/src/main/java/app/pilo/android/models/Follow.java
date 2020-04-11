package app.pilo.android.models;

public class Follow {
    private String created_at;
    private Artist artist;


    public Follow(){
        this.created_at = "";
        this.artist = new Artist();
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
