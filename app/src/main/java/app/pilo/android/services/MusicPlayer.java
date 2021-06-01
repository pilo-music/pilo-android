package app.pilo.android.services;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.SimpleExoPlayer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Download;
import app.pilo.android.models.Music;
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.Utils;

public class MusicPlayer implements iMusicPlayer {
    private final SimpleExoPlayer exoPlayer;
    private final AudioManager audioManager;
    private final UserSharedPrefManager userSharedPrefManager;
    private final PlayerService context;
    private final Handler handler;
    private final Runnable updateProgressAction = this::updateProgress;
    private final NotificationBuilder notificationBuilder;

    public static String CUSTOM_PLAYER_INTENT = "app.pilo.android.Services.custom_broadcast_intent";
    List<Music> musics;

    public MusicPlayer(PlayerService context, SimpleExoPlayer exoPlayer, AudioManager audioManager, NotificationBuilder notificationBuilder) {
        musics = new ArrayList<>();
        this.exoPlayer = exoPlayer;
        this.context = context;
        this.audioManager = audioManager;
        this.userSharedPrefManager = new UserSharedPrefManager(context);
        this.notificationBuilder = notificationBuilder;
        handler = new Handler();
    }


    @Override
    public void skip(boolean isNext) {
        List<Music> musics = AppDatabase.getInstance(context).musicDao().getAll();
        int activeIndex = findCurrentMusicIndex(musics);

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
    }

    @Override
    public void playTrack(List<Music> musics, String slug) {

        addMusicsToDB(musics);

        int result = audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return;
        }

        if (slug.equals(currentMusicSlug()) && isPlayerReady()) {
            this.togglePlay();
            return;
        }

        Music musicTable = AppDatabase.getInstance(context).musicDao().findById(slug);

        if (musicTable == null) {
            return;
        }

        // For playing from file if downloaded
        Download downloaded = AppDatabase.getInstance(context).downloadDao().findById(currentMusicSlug());
        if (downloaded != null && MusicDownloader.checkExists(context, musicTable, userSharedPrefManager.getStreamQuality())) {
            String downloadedFile = userSharedPrefManager.getStreamQuality().equals("320") ? downloaded.getPath128() : downloaded.getPath128();
            File file = new File(downloadedFile);
            Uri uri = Uri.fromFile(file);
            userSharedPrefManager.setActiveMusicSlug(currentMusicSlug());
            context.prepareExoPlayerFromURL(uri, slug, true);
        } else {
            String url = Utils.getMp3UrlForStreaming(context, musicTable);
            if (!url.equals("")) {
                context.prepareExoPlayerFromURL(Uri.parse(url), slug, true);
            }
        }

        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra("notify", true);
        context.sendBroadcast(intent);
        audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void seekTo(long progress) {
        exoPlayer.seekTo(progress);
    }

    @Override
    public Runnable updateProgress() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("progress", (int) exoPlayer.getCurrentPosition());
            intent.putExtra("max", (int) exoPlayer.getDuration());
            if (exoPlayer.getCurrentPosition() != 0) {
                context.sendBroadcast(intent);
            }

            long delayMs = TimeUnit.SECONDS.toMillis(1);
            if (exoPlayer.getPlayWhenReady()) {
                handler.postDelayed(updateProgressAction, delayMs);
            }
        }
        return this::updateProgress;
    }

    @Override
    public void togglePlay() {
        if (exoPlayer == null) {
            return;
        }
        if (exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("pause", true);
            context.sendBroadcast(intent);
        } else {
            exoPlayer.setPlayWhenReady(true);
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("play", true);
            context.sendBroadcast(intent);
            handler.post(updateProgressAction);
            if (audioManager != null) {
                audioManager.requestAudioFocus(context, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        notificationBuilder.show();
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

    private int findCurrentMusicIndex(List<Music> musics) {
        int activeIndex = -1;
        for (int i = 0; i < musics.size(); i++) {
            if (musics.get(i).getSlug().equals(currentMusicSlug())) {
                activeIndex = i;
            }
        }
        return activeIndex;
    }

    private String currentMusicSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private void addMusicsToDB(List<Music> musicList) {
        AppDatabase.getInstance(context).musicDao().nukeTable();
        AppDatabase.getInstance(context).musicDao().insertAll(musicList);
        musics.clear();
        musics.addAll(musicList);
    }
}
