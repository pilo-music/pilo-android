package app.pilo.android.services.MediaSession;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

import app.pilo.android.services.MusicPlayer;

public class MediaSessionCallback extends MediaSessionCompat.Callback {
    private final MusicPlayer musicPlayer;
    int d = 0;

    public MediaSessionCallback(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public boolean onMediaButtonEvent(Intent intent) {
        String intentAction = intent.getAction();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            return false;
        }
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (action == KeyEvent.ACTION_DOWN) {
                    d++;
                    Handler handler = new Handler();
                    Runnable r = () -> {
                        if (d == 1) {
                            musicPlayer.togglePlay();
                        }
                        if (d == 2) {
                            musicPlayer.skip(true);
                        }
                        d = 0;
                    };
                    if (d == 1) {
                        handler.postDelayed(r, 500);
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                if (action == KeyEvent.ACTION_DOWN) {
                    musicPlayer.togglePlay();
                }
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_NEXT: {
                if (action == KeyEvent.ACTION_DOWN) {
                    musicPlayer.skip(true);
                }
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
                if (action == KeyEvent.ACTION_DOWN) {
                    musicPlayer.skip(false);
                }
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                if (action == KeyEvent.ACTION_DOWN || action == KeyEvent.ACTION_MULTIPLE) {
                    musicPlayer.seekTo(musicPlayer.getCurrentMusicPosition() + 5000);
                }
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_REWIND: {
                if (action == KeyEvent.ACTION_DOWN || action == KeyEvent.ACTION_MULTIPLE) {
                    if (musicPlayer.getCurrentMusicPosition() > 6000) {
                        musicPlayer.seekTo(musicPlayer.getCurrentMusicPosition() - 5000);
                    }
                }
                break;
            }

        }
        return super.onMediaButtonEvent(intent);
    }
}
