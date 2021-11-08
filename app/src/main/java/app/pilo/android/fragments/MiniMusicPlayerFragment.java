package app.pilo.android.fragments;

import static app.pilo.android.services.MusicPlayer.Constant.CUSTOM_PLAYER_INTENT;

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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.databinding.FragmentMiniMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.services.MusicPlayer.Constant;
import app.pilo.android.utils.PlayButtonAnimation;

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
        return view;
    }

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
                    handleIncomingBroadcast(intent);
                }
            }
        }, intentToReceiveFilter, null, mHandler);
        active = true;
    }

    private void handleIncomingBroadcast(Intent intent) {
        if (binding == null) {
            return;
        }

        Bundle extras = intent.getExtras();
        Set<String> ks = extras.keySet();
        for (String key : ks) {
            switch (key) {
                case Constant.INTENT_NOTIFY:
                    initPlayerUi();
                case Constant.INTENT_PLAY:
                    binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
                    break;
                case Constant.INTENT_PAUSE:
                    binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
                    break;
            }
        }
//        musicLoading = intent.getBooleanExtra(Constant.INTENT_LOADING, false);
    }

    private void initPlayerUi() {
        if (!getCurrentSlug().equals("")) {
            if (musicLoading) {
                binding.imgMusicPlayerCollapsedPlay.setVisibility(View.GONE);
                binding.llExtendedMusicPlayerLoading.setVisibility(View.VISIBLE);
                return;
            }

            if (isPlayerReady()) {
                binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
            } else {
                binding.imgMusicPlayerCollapsedPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
            }
            Music music = AppDatabase.getInstance(context).musicDao().findById(getCurrentSlug());
            if (music != null) {
                binding.tvMusicPlayerCollapsedTitle.setText(music.getTitle());
                binding.tvMusicPlayerCollapsedArtist.setText(music.getArtist().getName());
                binding.imgMusicPlayerCollapsedPlay.setVisibility(View.VISIBLE);
                binding.llExtendedMusicPlayerLoading.setVisibility(View.GONE);

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
            playButtonAnimation.showBonceAnimation(binding.imgMusicPlayerCollapsedPlay);
            new Handler().postDelayed(() -> musicModule.getMusicPlayer().togglePlay(), 400);
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
            musicModule.getMusicPlayer().skip(false);
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
