package app.pilo.android.event;

import java.util.List;

import app.pilo.android.models.Music;

public class MusicRelatedEvent {

    public String musicSlug;
    public List<Music> musics;
    public boolean playWhenReady;

    public MusicRelatedEvent(List<Music> musicListItems, String musicSlug, boolean playWhenReady) {
        musics = musicListItems;
        this.playWhenReady = playWhenReady;
        this.musicSlug = musicSlug;
    }
}
