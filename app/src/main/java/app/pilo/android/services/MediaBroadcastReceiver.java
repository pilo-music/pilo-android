package app.pilo.android.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.pilo.android.services.MusicPlayer.MusicPlayer;

public class MediaBroadcastReceiver extends BroadcastReceiver {
    private final MusicPlayer musicPlayer;

    public MediaBroadcastReceiver(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null || musicPlayer.getExoPlayer() == null || !musicPlayer.getExoPlayer().getPlayWhenReady() || isInitialStickyBroadcast()) {
            return;
        }

        switch (intent.getAction()) {
            case Intent.ACTION_HEADSET_PLUG: {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        musicPlayer.togglePlay();
                        break;
                    case 1:
                        break;
                }
                break;
            }
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        musicPlayer.togglePlay();
                        break;
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
                break;
            }
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                musicPlayer.togglePlay();
                break;
        }
    }
}
