package app.pilo.android.services;

import android.content.Context;
import android.media.AudioManager;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import app.pilo.android.services.MediaSession.MediaSession;

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

    private SimpleExoPlayer initExoPlayer(){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
        DefaultLoadControl loadControl = new DefaultLoadControl(allocator, 600000, 8000000, 3000, 10000, -1, true);

        return ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
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
