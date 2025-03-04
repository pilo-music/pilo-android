package app.pilo.android.services.MusicPlayer;

import static app.pilo.android.services.MusicPlayer.Constant.CUSTOM_PLAYER_INTENT;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Download;
import app.pilo.android.models.Music;
import app.pilo.android.services.NotificationBuilder;
import app.pilo.android.services.PlayerService;
import app.pilo.android.utils.MusicDownloader;

public class MusicPlayer implements iMusicPlayer {
    private final SimpleExoPlayer exoPlayer;
    private final AudioManager audioManager;
    private final UserSharedPrefManager userSharedPrefManager;
    private final PlayerService context;
    private final NotificationBuilder notificationBuilder;
    private final MusicUtils utils;

    List<Music> musics;

    public MusicPlayer(PlayerService context, SimpleExoPlayer exoPlayer, AudioManager audioManager, NotificationBuilder notificationBuilder) {
        musics = new ArrayList<>();
        this.exoPlayer = exoPlayer;
        this.context = context;
        this.audioManager = audioManager;
        this.userSharedPrefManager = new UserSharedPrefManager(context);
        this.notificationBuilder = notificationBuilder;
        this.utils = new MusicUtils(context);
    }


    @Override
    public void skip(boolean isNext) {
        List<Music> musics = getCurrentPlaylist();
        int activeIndex = utils.findCurrentMusicIndex(musics);

        if (userSharedPrefManager.getShuffleMode()) {
            Random random = new Random();
            activeIndex = random.nextInt(musics.size());
        }

        if (isNext) {
            if (activeIndex != -1 && (activeIndex + 1) < musics.size()) {
                playTrack(musics, musics.get(activeIndex + 1).getSlug());
            } else if (musics.size() > 0) {
                playTrack(musics, musics.get(0).getSlug());
            }
        } else {
            if (exoPlayer != null && ((exoPlayer.getCurrentPosition() * 100) / exoPlayer.getDuration() > 5)) {
                exoPlayer.seekTo(0);
            } else {
                if ((activeIndex - 1) >= 0) {
                    playTrack(musics, musics.get(activeIndex - 1).getSlug());
                }
            }
        }

        sendIntent(isNext ? Constant.INTENT_NEXT : Constant.INTENT_PREVIOUS);
    }

    @Override
    public void playTrack(List<Music> musics, String slug, boolean loadRelative) {
        if (!loadRelative)
            this.playTrack(musics, slug);
        else
            this.utils.loadRelativeMusics(musics, slug);
    }


    @Override
    public void playTrack(List<Music> musics, String slug) {
        utils.addMusicsToDB(musics);
        int result = audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return;
        }

        if (slug.equals(currentMusicSlug()) && isPlayerReady()) {
            this.togglePlay();
            return;
        }

        Music music = AppDatabase.getInstance(context).musicDao().findById(slug);

        if (music == null) {
            return;
        }

        utils.addMusicToHistory(music);
        userSharedPrefManager.setActiveMusicSlug(slug);

        // For playing from file if downloaded
        Download downloaded = AppDatabase.getInstance(context).downloadDao().findById(slug);
        if (downloaded != null && MusicDownloader.checkExists(context, music, userSharedPrefManager.getStreamQuality())) {
            String downloadedFile = userSharedPrefManager.getStreamQuality().equals("320") ? downloaded.getPath320() : downloaded.getPath128();
            File file = MusicDownloader.getFile(context, downloadedFile);
            Uri uri = Uri.fromFile(file);
            context.prepareExoPlayerFromURL(uri, true);
        } else {
            String url = utils.getMp3UrlForStreaming(context, music);
            if (!url.equals("")) {
                context.prepareExoPlayerFromURL(Uri.parse(url), true);

            }
        }

        sendIntent(Constant.INTENT_NOTIFY);
        audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void seekTo(long progress) {
        exoPlayer.seekTo(progress);
    }

    @Override
    public void togglePlay() {
        if (exoPlayer == null) {
            return;
        }

        if (exoPlayer.getPlaybackState() == Player.STATE_IDLE) {
            List<Music> musics = getCurrentPlaylist();
            playTrack(musics, currentMusicSlug());
            return;
        }

        if (exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            sendIntent(Constant.INTENT_PAUSE);
        } else {
            exoPlayer.setPlayWhenReady(true);
            sendIntent(Constant.INTENT_PLAY);
            if (audioManager != null) {
                audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        notificationBuilder.show();
    }

    @Override
    public void ended() {
        switch (userSharedPrefManager.getRepeatMode()) {
            case Constant.REPEAT_MODE_NONE: {
                skip(true);
                break;
            }
            case Constant.REPEAT_MODE_ONE: {
                exoPlayer.seekTo(0);
                break;
            }
        }
    }


    public SimpleExoPlayer getExoPlayer() {
        return this.exoPlayer;
    }

    public boolean isPlayerReady() {
        return this.exoPlayer != null && this.exoPlayer.getPlayWhenReady();
    }

    public long getCurrentMusicPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return this.exoPlayer.getDuration();
    }

    private String currentMusicSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private List<Music> getCurrentPlaylist() {
        return AppDatabase.getInstance(context).musicDao().getAll();
    }

    private void sendIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra(action, true);
        context.sendBroadcast(intent);
    }
}
