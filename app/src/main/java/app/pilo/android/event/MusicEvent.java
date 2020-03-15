package app.pilo.android.event;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.models.Album;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;

public class MusicEvent {

    public final Music music;
    public final List<Music> musics = new ArrayList<>();

    public MusicEvent(Music music, Playlist playlist) {
        this.music = music;
        musics.addAll(playlist.getMusics());
    }

    public MusicEvent(Music music, Album album) {
        this.music = music;
        musics.addAll(album.getMusics());
    }

    public MusicEvent(Music music, List<Music> items) {
        this.music = music;
        musics.addAll(items);
    }
}
