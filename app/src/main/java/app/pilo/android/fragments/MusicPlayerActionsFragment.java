package app.pilo.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.android.volley.error.VolleyError;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.databinding.FragmentMusicPlayerActionsBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.dialogs.MusicActionsDialog;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MusicPlayerActionsFragment extends Fragment {
    private Context context;
    private LikeApi likeApi;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    private boolean likeProcess = false;

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
        likeApi = new LikeApi(context);
        utils = new Utils();
        setupService();
        initPlayerUi();
        setupViews();
        return view;
    }


    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT)) {
                initPlayerUi();
            }
        }
    };

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(mIntentReceiver, intentToReceiveFilter, null, null);
    }


    private void initPlayerUi() {
        if (binding == null) {
            return;
        }
        currentMusic = getCurrentMusic();
        if (currentMusic != null) {
            if (currentMusic.isHas_like()) {
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
            } else {
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
            }
        }
    }


    void setupViews() {
        binding.piloDb.setMusic(currentMusic);
        binding.llSync.setOnClickListener(view -> binding.piloDb.download());
        binding.llLike.setOnClickListener(view -> like());
        binding.imgMore.setOnClickListener(view -> new MusicActionsDialog(context, currentMusic).show(((MainActivity) (context)).getSupportFragmentManager(), MusicActionsDialog.TAG));
    }

    void like() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            if (currentMusic == null)
                return;

            if (likeProcess)
                return;
            if (!currentMusic.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(binding.imgLike);
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                likeApi.like(currentMusic.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getMainActivity(), message);
                            binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                        } else {
                            currentMusic.setHas_like(true);
                            AppDatabase.getInstance(context).musicDao().update(currentMusic);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getMainActivity());
                        binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                likeApi.like(currentMusic.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getMainActivity(), message);
                            binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                        } else {
                            currentMusic.setHas_like(false);
                            AppDatabase.getInstance(context).musicDao().update(currentMusic);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getMainActivity());
                        binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        }
    }


    private Music getCurrentMusic() {
        return AppDatabase.getInstance(context).musicDao().findById(getCurrentSlug());
    }

    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private SlidingUpPanelLayout getSlidingLayout() {
        return ((MainActivity) context).getSliding_layout();
    }

    private MainActivity getMainActivity() {
        return ((MainActivity) context);
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
