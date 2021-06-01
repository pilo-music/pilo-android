package app.pilo.android.services.MediaSession;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicPlayer.MusicPlayer;
import app.pilo.android.services.PlayerService;

public class MediaSession {

    private final PlaybackStateCompat.Builder mStateBuilder;
    private final MediaSessionCompat mMediaSession;
    private final UserSharedPrefManager userSharedPrefManager;
    private final Context context;
    private final MusicPlayer musicPlayer;

    public MediaSession(Context context, MusicPlayer musicPlayer) {
        mStateBuilder = new PlaybackStateCompat.Builder();
        this.context = context;
        mMediaSession = new MediaSessionCompat(context, "pilo");
        userSharedPrefManager = new UserSharedPrefManager(context);
        this.musicPlayer = musicPlayer;
    }


    public void updateMediaSessionMetaData() {
        mMediaSession.setCaptioningEnabled(true);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, PlayerService.class), 0);
        if (pi != null) {
            mMediaSession.setMediaButtonReceiver(pi);
        }

        mMediaSession.setCallback(new MediaSessionCallback(musicPlayer));
        mMediaSession.setActive(true);
        initMediaSession();
    }


    private void initMediaSession() {
        boolean isPlayReady =musicPlayer.getExoPlayer() != null && musicPlayer.getExoPlayer().getPlayWhenReady();
        int playState = isPlayReady ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING;

        Music musicTable = getMusicTable();
        if (musicTable != null) {
            String artist = "";
            if (musicTable.getArtist() != null) {
                artist = musicTable.getArtist().getName();
            }

            mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                    .putString(MediaMetadata.METADATA_KEY_ALBUM, "")
                    .putString(MediaMetadata.METADATA_KEY_TITLE, musicTable.getTitle())
                    .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, 0)
                    .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 1)
                    .build());
        }

        mStateBuilder.setState(playState, 0, 1.0f)
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    private Music getMusicTable() {
        return AppDatabase.getInstance(context).musicDao().findById(userSharedPrefManager.getActiveMusicSlug());
    }
}
