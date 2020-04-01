package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class SingleArtist {

    private Artist artist;
    private List<Music> best_musics;
    private List<Music> last_musics;
    private List<Album> albums;
    private List<Video> videos;
    private List<Playlist> playlists;
    private boolean is_follow;

    public SingleArtist() {
        this.best_musics = new ArrayList<>();
        this.last_musics = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.artist = new Artist();
        this.is_follow = false;
    }

    public SingleArtist(
            Artist artist,
            List<Music> best_musics,
            List<Music> last_musics,
            List<Album> albums,
            List<Video> videos,
            List<Playlist> playlists,
            boolean is_follow
    ) {
        this.artist = artist;
        this.best_musics = best_musics;
        this.last_musics = last_musics;
        this.albums = albums;
        this.videos = videos;
        this.playlists = playlists;
        this.is_follow = is_follow;
    }

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

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public boolean isIs_follow() {
        return is_follow;
    }

    public void setIs_follow(boolean is_follow) {
        this.is_follow = is_follow;
    }
}
