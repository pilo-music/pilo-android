package app.pilo.android.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import app.pilo.android.helpers.UserSharedPrefManager;

import static app.pilo.android.services.MusicPlayer.CUSTOM_PLAYER_INTENT;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private boolean mReceiversRegistered = false;

    private IBinder mBinder;
    private Handler mHandler;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaBroadcastReceiver mediaBroadcastReceiver;
    private UserSharedPrefManager userSharedPrefManager;

    private MusicModule musicModule;

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
        musicModule.getNotificationBuilder().show();
    }

    private void init() {
        mBinder = new LocalBinder();
        this.musicModule = new MusicModule(this);
        mediaBroadcastReceiver = new MediaBroadcastReceiver(musicModule.getMusicPlayer());
        this.userSharedPrefManager = new UserSharedPrefManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mediaBroadcastReceiver, filter);
        mReceiversRegistered = true;
        mStateBuilder = new PlaybackStateCompat.Builder();
        musicModule.getMediaSession().updateMediaSessionMetaData();

        if (intent != null) {

            if (intent.getBooleanExtra("start", false)) {
                musicModule.getMusicPlayer().togglePlay();
            } else if (intent.getAction() != null && intent.getAction().equals("toggle")) {
                musicModule.getMusicPlayer().togglePlay();
            } else if (intent.getAction() != null && intent.getAction().equals("next")) {
                musicModule.getMusicPlayer().skip(true);
            } else if (intent.getAction() != null && intent.getAction().equals("previous")) {
                musicModule.getMusicPlayer().skip(false);
            } else if (intent.getAction() != null && intent.getAction().equals("close")) {
                stopPiloService();
            } else {
                musicModule.getNotificationBuilder().show();
            }
        }

        try {
            MediaButtonReceiver.handleIntent(musicModule.getMediaSession().getMediaSession(), intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (!musicModule.getMusicPlayer().isPlayerReady()) {
            return;
        }
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            getExpoPlayer().setVolume(0.3f);
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            musicModule.getMusicPlayer().togglePlay();
            musicModule.getAudioManager().abandonAudioFocus(this);
        } else if (focusChange ==
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            getExpoPlayer().setVolume(0.3f);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE ||
                focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
            getExpoPlayer().setVolume(1f);
        }
    }

    public void prepareExoPlayerFromURL(Uri uri, String music_slug, boolean play_when_ready) {
        userSharedPrefManager.setActiveMusicSlug(music_slug);
        if (getExpoPlayer() == null) {
            musicModule = new MusicModule(this);
        }

        musicModule.getMediaSession().updateMediaSessionMetaData();

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "pilo"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        getExpoPlayer().setPlayWhenReady(play_when_ready);
        getExpoPlayer().prepare(audioSource);

        getExpoPlayer().setVolume(1);
        if (play_when_ready) {
            if (musicModule.getAudioManager() != null) {
                musicModule.getAudioManager().requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        getExpoPlayer().addListener(new Player.EventListener() {

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == STATE_ENDED) {
                    musicModule.getMusicPlayer().skip(false);
                } else if (playbackState == STATE_READY) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, getExpoPlayer().getCurrentPosition(), 1f);
                    musicModule.getMediaSession().getMediaSession().setPlaybackState(mStateBuilder.build());
                } else if (playbackState == STATE_BUFFERING) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, getExpoPlayer().getCurrentPosition(), 1f);
                    musicModule.getMediaSession().getMediaSession().setPlaybackState(mStateBuilder.build());
                }
            }
        });
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.post(musicModule.getMusicPlayer().updateProgress());


        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra("notify", true);
        sendBroadcast(intent);

        musicModule.getNotificationBuilder().show();
    }


    public class LocalBinder extends Binder {
        public PlayerService getServerInstance() {
            return PlayerService.this;
        }
    }

    public MusicModule getMusicModule() {
        return this.musicModule;
    }

    public SimpleExoPlayer getExpoPlayer(){
        return musicModule.getMusicPlayer().getExoPlayer();
    }

    private void stopPiloService() {
        Intent i = new Intent();
        i.setAction(CUSTOM_PLAYER_INTENT);
        i.putExtra("close", true);
        sendBroadcast(i);
        musicModule.getNotificationBuilder().cancelAll();
        if (getExpoPlayer() != null) {
            getExpoPlayer().setPlayWhenReady(false);
            getExpoPlayer().release();
        }
        if (mReceiversRegistered) {
            unregisterReceiver(mediaBroadcastReceiver);
            mReceiversRegistered = false;
        }
        stopForeground(true);
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

}

