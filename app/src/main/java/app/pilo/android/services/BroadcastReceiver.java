package app.pilo.android.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

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
        } else if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
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

            } else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                if (exoPlayer != null && exoPlayer.getPlayWhenReady() && !isInitialStickyBroadcast()) {
                    togglePlay();
                }
            }
        }
    }
}