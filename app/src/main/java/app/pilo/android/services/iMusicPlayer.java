package app.pilo.android.services;

import android.net.Uri;

public interface iMusicPlayer {

    void skip(boolean next);

    void playTrack(String slug);

    void prepareExoPlayerFromURL(Uri url,String slug,boolean playWhenReady);

    void seekTo(int progress);

    void updateProgress();

    void togglePlay();
}
