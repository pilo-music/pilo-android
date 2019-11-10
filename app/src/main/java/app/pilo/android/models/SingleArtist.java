package app.pilo.android.models;

import java.util.List;

public class SingleArtist {
    private List<Music> best_musics;
    private List<Music> last_musics;
    private List<Album> albums;
    private List<Video> videos;
    private Artist artist;

    public List<Music> getBest_musics() {
        return best_musics;
    }

    public void setBest_musics(List<Music> best_musics) {
        this.best_musics = best_musics;
    }

    public List<Music> getLast_musics() {
        return last_musics;
    }

    public void setLast_musics(List<Music> last_musics) {
        this.last_musics = last_musics;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
