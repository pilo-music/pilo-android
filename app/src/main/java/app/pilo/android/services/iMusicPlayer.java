package app.pilo.android.services;

import android.net.Uri;

public interface iMusicPlayer {

    void skip(boolean next);

    void playTrack(String slug);

    void seekTo(long progress);

    Runnable updateProgress();

    void togglePlay();
}
