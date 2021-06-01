package app.pilo.android.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.databinding.FragmentMiniMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.services.PlayerService;
import app.pilo.android.utils.PlayButtonAnimation;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MiniMusicPlayerFragment extends Fragment {

    private final Handler mHandler = new Handler();
    private boolean active = false;
    private final PlayButtonAnimation playButtonAnimation = new PlayButtonAnimation();
    private MusicModule musicModule;
    private UserSharedPrefManager userSharedPrefManager;
    private Context context;

    private FragmentMiniMusicPlayerBinding binding;

    public MiniMusicPlayerFragment() {
        super(R.layout.fragment_mini_music_player);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMiniMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        this.userSharedPrefManager = new UserSharedPrefManager(context);
        setupService();
        return view;
    }

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        active = true;

        Intent player_service_intent = new Intent(context, PlayerService.class);
        context.bindService(player_service_intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
                handleIncomingBroadcast(intent);
            }
        }
    };


    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder mLocalBinder = (PlayerService.LocalBinder) service;
            PlayerService playerService = mLocalBinder.getServerInstance();
            if (playerService != null && active) {
                musicModule = playerService.getMusicModule();
                setupControls();
                setupSlidingUpPanel();
                initPlayerUi();
            }
        }
    };


    private void handleIncomingBroadcast(Intent intent) {
        if (binding == null) {
            return;
        }

        if (intent.getBooleanExtra("play", false)) {
            binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
        } else if (intent.getBooleanExtra("pause", false)) {
            binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
        }
        initPlayerUi();
    }

    private void initPlayerUi() {
        if (binding == null)
            return;

        if (!getCurrentSlug().equals("")) {
            if (isPlayerReady()) {
                binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
            } else {
                binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
            }
            Music music = AppDatabase.getInstance(context).musicDao().findById(getCurrentSlug());
            if (music != null) {
                binding.tvMusicPlayerCollapsedTitle.setText(music.getTitle());
                binding.tvMusicPlayerCollapsedArtist.setText(music.getArtist().getName());
                Glide.with(context)
                        .load(music.getImage())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.rivMusicPlayerCollapsedImage);
            }
        }
    }


    private void setupControls() {
        if (binding == null) {
            return;
        }
        binding.imgMusicPlayerCollapsedPlay.setOnClickListener(v -> {
            musicModule.getMusicPlayer().togglePlay();
            playButtonAnimation.showBonceAnimation(binding.imgMusicPlayerCollapsedPlay);
        });
        binding.imgMusicPlayerCollapsedNext.setOnClickListener(v -> musicModule.getMusicPlayer().skip(true));
        binding.imgMusicPlayerCollapsedPrev.setOnClickListener(v -> {
            if (((musicModule.getMusicPlayer().getCurrentMusicPosition() * 100) / musicModule.getMusicPlayer().getDuration() > 5)) {
                musicModule.getMusicPlayer().seekTo(0);
            } else {
                musicModule.getMusicPlayer().skip(false);
            }
        });
    }


    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private boolean isPlayerReady() {
        return musicModule.getMusicPlayer().isPlayerReady();
    }

    private void setupSlidingUpPanel() {
        if (binding == null)
            return;

        binding.llPageHeader.setAlpha(0);
        getSlidingLayout().addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                binding.llMusicPlayerCollapsed.setAlpha(1 - slideOffset);
                binding.llPageHeader.setAlpha(0 + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private SlidingUpPanelLayout getSlidingLayout() {
        return ((MainActivity) context).getSliding_layout();
    }

}
