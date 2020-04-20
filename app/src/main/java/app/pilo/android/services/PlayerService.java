package app.pilo.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Html;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Constant;
import app.pilo.android.utils.Utils;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {
    public static String CUSTOM_PLAYER_INTENT = "app.pilo.android.Services.custom_broadcast_intent";

    private boolean mReceiversRegistered = false;
    private AudioManager mAudioManager;
    private static final int NOTIF_ID = 32432;

    private BroadcastReceiver button_reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:

                        if (exoPlayer != null && exoPlayer.getPlayWhenReady() && !isInitialStickyBroadcast()) {
                            togglePlay();
                        }
                        break;
                    case 1:

                        break;
                }
            }else if (intent != null && intent.getAction() != null){
                if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch(state) {
                        case BluetoothAdapter.STATE_OFF:
                            if (exoPlayer != null && exoPlayer.getPlayWhenReady() && !isInitialStickyBroadcast()) {
                                togglePlay();
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if (exoPlayer != null && exoPlayer.getPlayWhenReady() && !isInitialStickyBroadcast()) {
                                togglePlay();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:

                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:

                            break;
                    }

                }else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    if (exoPlayer != null && exoPlayer.getPlayWhenReady() && !isInitialStickyBroadcast()) {
                        togglePlay();
                    }
                }
            }
        }
    };
    SimpleExoPlayer exoPlayer;
    IBinder mBinder = new LocalBinder();
    private UserSharedPrefManager sessionManager;
    private Handler mHandler;
    private String current_music_slug = "";
    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        init();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        showNotification();
    }

    @Override
    public void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.stop(true);
            exoPlayer.release();
        }
        if (mReceiversRegistered) {
            unregisterReceiver(button_reciever);
            mReceiversRegistered = false;
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
        }
        if (notifManager != null) {
            notifManager.cancelAll();
        }

        this.stopForeground(true);

        super.onDestroy();

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (exoPlayer != null) {
            exoPlayer.stop(true);
            exoPlayer.release();
        }
        if (mReceiversRegistered) {
            unregisterReceiver(button_reciever);
            mReceiversRegistered = false;
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
        }
        if (notifManager != null) {
            notifManager.cancelAll();
        }

        this.stopForeground(true);
        super.onTaskRemoved(rootIntent);

    }

    PlaybackStateCompat.Builder mStateBuilder;
    MediaSessionCompat mMediaSession;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(button_reciever, filter);
        mReceiversRegistered = true;
        mMediaSession = new MediaSessionCompat(this, "casset");

        mStateBuilder = new PlaybackStateCompat.Builder();
        updateMediaSessionMetaData();

        if (intent != null) {

            if (intent.getBooleanExtra("start", false)) {
                togglePlay();
            } else if (intent.getAction() != null && intent.getAction().equals("toggle")) {
                togglePlay();
            } else if (intent.getAction() != null && intent.getAction().equals("next")) {
                skipToNext();
            } else if (intent.getAction() != null && intent.getAction().equals("previous")) {
                skipToPrevious();
            } else if (intent.getAction() != null && intent.getAction().equals("close")) {
                Intent i = new Intent();
                i.setAction(CUSTOM_PLAYER_INTENT);
                i.putExtra("close", true);
                sendBroadcast(i);
                notifManager.cancelAll();
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    exoPlayer = null;
                }
                if (mReceiversRegistered) {
                    unregisterReceiver(button_reciever);
                    mReceiversRegistered = false;
                }
                stopForeground(true);
                stopSelf();
            }else {
                showNotification();
            }
        }

        try{
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                exoPlayer.setVolume(0.3f);

            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                togglePlay();
            }
            mAudioManager.abandonAudioFocus(this);
        } else if (focusChange ==
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                exoPlayer.setVolume(0.3f);
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
            if (exoPlayer != null) {
                exoPlayer.setVolume(1f);
            }
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        int d = 0;

        @Override
        public boolean onMediaButtonEvent(Intent intent) {

            String intentAction = intent.getAction();
            if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                return false;
            }
            KeyEvent event = intent
                    .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int action = event.getAction();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    if (action == KeyEvent.ACTION_DOWN) {
                        d++;
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {

                                if (d == 1) {
                                    togglePlay();
                                }
                                if (d == 2) {
                                    skipToNext();
                                }
                                d = 0;
                            }
                        };
                        if (d == 1) {
                            handler.postDelayed(r, 500);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                    if (action == KeyEvent.ACTION_DOWN) {
                        togglePlay();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_PLAY: {
                    if (action == KeyEvent.ACTION_DOWN) {
                        togglePlay();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                    if (action == KeyEvent.ACTION_DOWN) {
                        togglePlay();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_NEXT: {
                    if (action == KeyEvent.ACTION_DOWN) {
                        skipToNext();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
                    if (action == KeyEvent.ACTION_DOWN) {
                        skipToPrevious();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                    if (action == KeyEvent.ACTION_DOWN || action == KeyEvent.ACTION_MULTIPLE) {

                        exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 5000);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_MEDIA_REWIND: {
                    if (action == KeyEvent.ACTION_DOWN || action == KeyEvent.ACTION_MULTIPLE) {
                        if (exoPlayer.getCurrentPosition() > 6000) {
                            exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 5000);
                        }
                    }
                    break;
                }

            }
            return super.onMediaButtonEvent(intent);
        }


    }

    NotificationManager notifManager;

    private void init() {
        sessionManager = new UserSharedPrefManager(this);
        notifManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void skipToNext() {
        List<Music> current_items = AppDatabase.getInstance(this).musicDao().getAll();

            int active_index = -1;
            for (int i = 0; i < current_items.size(); i++) {
                if (current_items.get(i).getSlug().equals(sessionManager.getActiveMusicSlug())) {
                    active_index = i;
                }
            }
            if (active_index != -1 && (active_index + 1) < current_items.size()) {
                playTrack(current_items.get(active_index + 1).getSlug());
            } else if (current_items.size() > 0) {
                playTrack(current_items.get(0).getSlug());
            }


    }

    private void playTrack(String music_slug) {


        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return;
        }

        Music musicTable = AppDatabase.getInstance(this).musicDao().findById(music_slug);

        if (musicTable == null) {
            return;
        }

// -------------       For playing from file if downloaded
    /*
      QueryBuilder<DownloadTable> builder = downloadTableBox.query();

     QueryBuilder<DownloadTable> builder = downloadTableBox.query();
        builder.link(DownloadTable_.music).equal(MusicTable_.music_id, music_id);
        DownloadTable download_table = builder.build().findFirst();
        File musicFile = null;
        if (download_table != null) {
            File music_folder = func.findMusicFolder(PlayerService.this, music_id, download_table.quality);
            if (music_folder.isDirectory()) {
                musicFile = new File(music_folder, download_table.music.music_id + ".mp3");
            }
        }
        if (musicFile != null && musicFile.exists()) {
            Uri uri = Uri.fromFile(musicFile);
            sessionManager.setActiveMusicId(music_id);
            prepareExoPlayerFromURL(uri, music_id, true);
        } else {
            String url = func.getMp3UrlForStreaming(musicTable);
            if (!url.equals("")) {
                sessionManager.setActiveMusicId(music_id);
                prepareExoPlayerFromURL(Uri.parse(url), music_id, true);
            }
        }*/

        String url = Utils.getMp3UrlForStreaming(this, musicTable);
        if (!url.equals("")) {
            sessionManager.setActiveMusicSlug(music_slug);
            prepareExoPlayerFromURL(Uri.parse(url), music_slug, true);
        }
        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra("notify", true);
        sendBroadcast(intent);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void skipToPrevious() {
        if (exoPlayer != null && ((exoPlayer.getCurrentPosition() * 100) / exoPlayer.getDuration() > 5)) {
            exoPlayer.seekTo(0);
        }else {
            List<Music> current_items = AppDatabase.getInstance(this).musicDao().getAll();

                int active_index = -1;
                for (int i = 0; i < current_items.size(); i++) {
                    if (current_items.get(i).getSlug().equals(sessionManager.getActiveMusicSlug())) {
                        active_index = i;
                    }
                }
                if ((active_index - 1) >= 0) {
                    playTrack(current_items.get(active_index - 1).getSlug());
                }
        }
    }

    public void prepareExoPlayerFromURL(Uri uri, String music_slug, boolean play_when_ready) {

        current_music_slug = music_slug;
        if (exoPlayer != null) {
            exoPlayer.release();
        }

        updateMediaSessionMetaData();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
        DefaultLoadControl loadControl = new DefaultLoadControl(allocator, 600000, 8000000, 3000, 10000, -1, true);


        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "casset"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.setPlayWhenReady(play_when_ready);
        exoPlayer.prepare(audioSource);

        exoPlayer.setVolume(1);
        if (play_when_ready) {
            if (mAudioManager != null) {
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        exoPlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == STATE_ENDED) {
                    skipToNext();
                } else if (playbackState == STATE_READY) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, exoPlayer.getCurrentPosition(), 1f);
                    mMediaSession.setPlaybackState(mStateBuilder.build());
                } else if (playbackState == STATE_BUFFERING) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, exoPlayer.getCurrentPosition(), 1f);
                    mMediaSession.setPlaybackState(mStateBuilder.build());
                }
            }
        });
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.post(updateProgressAction);


        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra("notify", true);
        sendBroadcast(intent);

        showNotification();
    }

    public void seekTo(int progress) {
        exoPlayer.seekTo(progress);
    }

    public void togglePlay() {

        if (exoPlayer == null) {
            return;
        }
        if (exoPlayer.getPlayWhenReady()) {
            exoPlayer.setPlayWhenReady(false);
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("pause", true);
            sendBroadcast(intent);
            showNotification();
        } else {
            exoPlayer.setPlayWhenReady(true);
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("play", true);
            sendBroadcast(intent);
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.post(updateProgressAction);
            if (mAudioManager != null) {
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
            showNotification();
        }
    }

    public class LocalBinder extends Binder {
        public PlayerService getServerInstance() {
            return PlayerService.this;
        }
    }

    private void updateProgress() {

        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("progress", (int) exoPlayer.getCurrentPosition());
            intent.putExtra("max", (int) exoPlayer.getDuration());
            if (exoPlayer.getCurrentPosition() != 0) {
                sendBroadcast(intent);
            }

            long delayMs = TimeUnit.SECONDS.toMillis(1);
            if (exoPlayer.getPlayWhenReady()) {
                mHandler.postDelayed(updateProgressAction, delayMs);
            }
        }
    }


    public ExoPlayer getPlayer() {
        return exoPlayer;
    }

    public String getCurrent_music_slug() {
        return current_music_slug;
    }

    private void showNotification() {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MEDIA_BUTTON);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for Play
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction("toggle");
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);


        //Intent for next
        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction("next");
        PendingIntent nextPIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        //Intent for previous
        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction("previous");
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        //Intent for close
        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction("close");
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_casset_channel";// The id of the channel.
            CharSequence name = "casset";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notifManager.createNotificationChannel(mChannel);
        }

        int play_pause_icon = 0;

        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            play_pause_icon = R.drawable.ic_pause_icon;
        } else {
            play_pause_icon = R.drawable.ic_play_icon;
        }

        Music music = AppDatabase.getInstance(this).musicDao().findById(current_music_slug);
        if (music != null) {
            String artist_name = "";
            if (music.getArtist() != null) {
                artist_name = music.getArtist().getName();
            }
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(PlayerService.this, "my_casset_channel")
                    .setContentTitle(Html.fromHtml(music.getTitle()))
                    .setContentText(Html.fromHtml(artist_name))
                    .setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_pilo_logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                    .setOngoing(true)
                    .addAction(R.drawable.ic_previous, "", ppreviousIntent)
                    .addAction(play_pause_icon, "", pplayIntent)
                    .addAction(R.drawable.ic_next, "", nextPIntent)
                    .addAction(R.drawable.ic_close, "", pcloseIntent);


            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(NOTIF_ID, mNotificationBuilder.build());
            } else {
                notifManager.notify(NOTIF_ID,
                        mNotificationBuilder.build());
            }
            int largeIconSize = Math.round(64 * getResources().getDisplayMetrics().density);
            Glide.with(this).asBitmap().load(music.getImage()).override(largeIconSize, largeIconSize)
                    .placeholder(R.drawable.placeholder_song).into(new BaseTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    mNotificationBuilder.setLargeIcon(resource);
                    notifManager.notify(NOTIF_ID,
                            mNotificationBuilder.build());
                }

                @Override
                public void getSize(@NonNull SizeReadyCallback cb) {
                    cb.onSizeReady(largeIconSize, largeIconSize);

                }

                @Override
                public void removeCallback(@NonNull SizeReadyCallback cb) {

                }
            });


        } else {
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(PlayerService.this, "my_casset_channel")
                    .setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_pilo_logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                    .setOngoing(true)
                    .addAction(R.drawable.ic_previous, "", ppreviousIntent)
                    .addAction(play_pause_icon, "", pplayIntent)
                    .addAction(R.drawable.ic_next, "", nextPIntent)
                    .addAction(R.drawable.close, "", pcloseIntent);

            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(NOTIF_ID, mNotificationBuilder.build());
            } else {
                notifManager.notify(NOTIF_ID,
                        mNotificationBuilder.build());
            }
        }
    }


    private void updateMediaSessionMetaData() {
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCaptioningEnabled(true);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(this, PlayerService.class), 0);
        if (pi != null) {
            mMediaSession.setMediaButtonReceiver(pi);
        }


        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
        int playState = PlaybackStateCompat.STATE_PAUSED;
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            playState = PlaybackStateCompat.STATE_PLAYING;
        }

        Music musicTable = AppDatabase.getInstance(this).musicDao().findById(current_music_slug);

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
    }

