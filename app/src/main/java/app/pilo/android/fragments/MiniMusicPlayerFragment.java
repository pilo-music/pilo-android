package app.pilo.android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.Player;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.databinding.FragmentMiniMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.utils.PlayButtonAnimation;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MiniMusicPlayerFragment extends Fragment {

    private final Handler mHandler = new Handler();
    private boolean active = false;
    private final PlayButtonAnimation playButtonAnimation = new PlayButtonAnimation();
    private final MusicModule musicModule;
    private UserSharedPrefManager userSharedPrefManager;
    private Context context;
    private boolean musicLoading = false;

    private FragmentMiniMusicPlayerBinding binding;

    public MiniMusicPlayerFragment(MusicModule musicModule) {
        super(R.layout.fragment_mini_music_player);
        this.musicModule = musicModule;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMiniMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        this.userSharedPrefManager = new UserSharedPrefManager(context);
        setupService();
        setupControls();
        setupSlidingUpPanel();
        initPlayerUi();
        setPlayerChangeState();
        return view;
    }

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        active = true;
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
                handleIncomingBroadcast(intent);
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

        if (intent.hasExtra("loading")) {
            musicLoading = intent.getBooleanExtra("loading", false);
        }
        initPlayerUi();
    }

    private void initPlayerUi() {
        if (binding == null || musicLoading)
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
            if (musicLoading) {
                return;
            }
            musicModule.getMusicPlayer().togglePlay();
            playButtonAnimation.showBonceAnimation(binding.imgMusicPlayerCollapsedPlay);
        });
        binding.imgMusicPlayerCollapsedNext.setOnClickListener(v -> {
            if (musicLoading) {
                return;
            }
            musicModule.getMusicPlayer().skip(true);
        });
        binding.imgMusicPlayerCollapsedPrev.setOnClickListener(v -> {
            if (musicLoading) {
                return;
            }

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

    void setPlayerChangeState() {
        musicModule.getMusicPlayer().getExoPlayer().addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    binding.imgMusicPlayerCollapsedPlay.setVisibility(View.GONE);
                    binding.llExtendedMusicPlayerLoading.setVisibility(View.VISIBLE);
                } else {
                    binding.imgMusicPlayerCollapsedPlay.setVisibility(View.VISIBLE);
                    binding.llExtendedMusicPlayerLoading.setVisibility(View.GONE);
                }
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
