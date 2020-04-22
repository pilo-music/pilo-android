package app.pilo.android.models;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private String recommend;
    private List<Music> musics;
    private List<Artist> artists;
    private List<Video> videos;
    private List<Album> albums;
    private List<Playlist> playlists;

    public Search() {
        recommend = "";
        musics = new ArrayList<>();
        artists = new ArrayList<>();
        videos = new ArrayList<>();
        albums = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
}
