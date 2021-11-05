package app.pilo.android.services.MusicPlayer;

import android.net.Uri;

import java.util.List;

import app.pilo.android.models.Music;

public interface iMusicPlayer {

    void skip(boolean next);

    void playTrack(List<Music> musics, String slug);

    void playTrack(List<Music> musics, String slug, boolean loadRelative);

    void seekTo(long progress);

    Runnable updateProgress();

    void togglePlay();

    void ended();
}
