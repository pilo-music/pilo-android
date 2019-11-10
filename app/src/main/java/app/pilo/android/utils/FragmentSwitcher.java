package app.pilo.android.utils;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;


public class FragmentSwitcher {
    private Bundle bundle;
    private Fragment fragment;
    private Context context;

    public FragmentSwitcher(Context context, Fragment fragment, Bundle bundle) {
        this.bundle = bundle;
        this.context = context;
        this.fragment = fragment;
        this.fragmentJump();
    }

    private void fragmentJump() {
        if (bundle != null)
            fragment.setArguments(bundle);

        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(R.id.framelayout, fragment);
        }
    }

}
