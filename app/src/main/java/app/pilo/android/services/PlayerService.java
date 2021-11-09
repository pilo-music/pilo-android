package app.pilo.android.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import static app.pilo.android.services.MusicPlayer.Constant.CUSTOM_PLAYER_INTENT;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

import app.pilo.android.services.MusicPlayer.Constant;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private boolean mReceiversRegistered = false;

    private IBinder mBinder;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaBroadcastReceiver mediaBroadcastReceiver;
    private MusicModule musicModule;
    private boolean isPlayerEnded = false;

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


        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constant.INTENT_TOGGLE:
                    musicModule.getMusicPlayer().togglePlay();
                    break;
                case Constant.INTENT_NEXT:
                    musicModule.getMusicPlayer().skip(true);
                    break;
                case Constant.INTENT_PREVIOUS:
                    musicModule.getMusicPlayer().skip(false);
                    break;
                case Constant.INTENT_CLOSE:
                    stopPiloService();
                    break;
                default:
                    musicModule.getNotificationBuilder().show();
                    break;
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

    public void prepareExoPlayerFromURL(Uri uri, boolean play_when_ready) {
        if (getExpoPlayer() == null) {
            musicModule = new MusicModule(this);
        }

        musicModule.getMediaSession().updateMediaSessionMetaData();
        getExpoPlayer().setMediaItem(MediaItem.fromUri(uri));
        getExpoPlayer().setPlayWhenReady(play_when_ready);
        getExpoPlayer().prepare();
        getExpoPlayer().setVolume(1);
        if (play_when_ready) {
            if (musicModule.getAudioManager() != null) {
                musicModule.getAudioManager().requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        getExpoPlayer().addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == STATE_ENDED) {
                    if (!isPlayerEnded) {
                        musicModule.getMusicPlayer().ended();
                        isPlayerEnded = true;
                    }
                } else if (state == STATE_READY) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, getExpoPlayer().getCurrentPosition(), 1f);
                    musicModule.getMediaSession().getMediaSession().setPlaybackState(mStateBuilder.build());
                    isPlayerEnded = false;
                } else if (state == STATE_BUFFERING) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, getExpoPlayer().getCurrentPosition(), 1f);
                    musicModule.getMediaSession().getMediaSession().setPlaybackState(mStateBuilder.build());
                }
            }
        });
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

    public SimpleExoPlayer getExpoPlayer() {
        return musicModule.getMusicPlayer().getExoPlayer();
    }

    private void stopPiloService() {
        Intent i = new Intent();
        i.setAction(CUSTOM_PLAYER_INTENT);
        i.putExtra(Constant.INTENT_CLOSE, true);
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
        stopPiloService();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopPiloService();
    }

}

