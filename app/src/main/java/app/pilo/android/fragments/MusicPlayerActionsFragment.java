package app.pilo.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.databinding.FragmentMusicPlayerActionsBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.views.dialogs.MusicActionsDialog;


public class MusicPlayerActionsFragment extends Fragment {
    private Context context;
    private UserSharedPrefManager userSharedPrefManager;

    private Music currentMusic;
    private FragmentMusicPlayerActionsBinding binding;

    public MusicPlayerActionsFragment() {
        super(R.layout.fragment_music_player_actions);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerActionsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        userSharedPrefManager = new UserSharedPrefManager(context);
        initPlayerUi();
        setupViews();
        return view;
    }


    private void initPlayerUi() {
        if (binding == null) {
            return;
        }
        currentMusic = getCurrentMusic();
        binding.piloDb.setMusic(currentMusic);
        binding.piloLike.setMusic(currentMusic);
    }


    void setupViews() {
        binding.piloDb.setMusic(currentMusic);
        binding.llSync.setOnClickListener(view -> binding.piloDb.download());

        binding.piloLike.setMusic(currentMusic);
        binding.llLike.setOnClickListener(view -> binding.piloLike.callOnClick());

        binding.imgMore.setOnClickListener(view -> new MusicActionsDialog(context, currentMusic).show(((MainActivity) (context)).getSupportFragmentManager(), MusicActionsDialog.TAG));
    }


    private Music getCurrentMusic() {
        return AppDatabase.getInstance(context).musicDao().findById(getCurrentSlug());
    }

    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        initPlayerUi();
        binding.piloDb.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

}
