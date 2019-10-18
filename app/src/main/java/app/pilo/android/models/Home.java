package app.pilo.android.models;

import java.util.List;

public class Home {
    private List<Music> last_musics;
    private List<Album> albums;
    private List<Music> best_musics;
    private List<Video> videos;
    private List<Artist> artists;
    private List<HeroSlider> hero_sliders;


    public List<Music> getLast_music() {
        return last_musics;
    }

    public void setLast_music(List<Music> last_music) {
        this.last_musics = last_music;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Music> getBest_musics() {
        return best_musics;
    }

    public void setBest_musics(List<Music> best_musics) {
        this.best_musics = best_musics;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<HeroSlider> getHero_sliders() {
        return hero_sliders;
    }

    public void setHero_sliders(List<HeroSlider> hero_sliders) {
        this.hero_sliders = hero_sliders;
    }
}
