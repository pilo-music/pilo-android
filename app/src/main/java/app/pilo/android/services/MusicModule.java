package app.pilo.android.services;

import android.content.Context;
import android.media.AudioManager;
import com.google.android.exoplayer2.SimpleExoPlayer;
import app.pilo.android.services.MediaSession.MediaSession;
import app.pilo.android.services.MusicPlayer.MusicPlayer;

public class MusicModule {

    private final Context context;
    private final NotificationBuilder notificationBuilder;
    private final MusicPlayer musicPlayer;
    private final MediaSession mediaSession;
    private final AudioManager audioManager;

    public MusicModule(PlayerService context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        SimpleExoPlayer exoPlayer = initExoPlayer();
        this.notificationBuilder = new NotificationBuilder(context, exoPlayer);
        this.musicPlayer = new MusicPlayer(context, exoPlayer, audioManager,notificationBuilder);
        this.mediaSession = new MediaSession(context, musicPlayer);
    }

    public SimpleExoPlayer initExoPlayer(){
       return new SimpleExoPlayer.Builder(context).build();
    }


    public MusicPlayer getMusicPlayer() {
        return this.musicPlayer;
    }

    public MediaSession getMediaSession() {
        return this.mediaSession;
    }

    public NotificationBuilder getNotificationBuilder() {
        return notificationBuilder;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }
}
