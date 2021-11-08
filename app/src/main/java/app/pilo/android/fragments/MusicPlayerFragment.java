package app.pilo.android.fragments;

import static app.pilo.android.services.MusicPlayer.Constant.CUSTOM_PLAYER_INTENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.PlayerViewPagerAdapter;
import app.pilo.android.databinding.FragmentMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.services.MusicPlayer.MusicUtils;
import app.pilo.android.services.MusicPlayer.Constant;
import app.pilo.android.utils.PlayButtonAnimation;

public class MusicPlayerFragment extends Fragment {

    private boolean is_seeking;
    private final Handler mHandler = new Handler();
    private boolean active = false;

    private List<Music> musics;
    private final PlayButtonAnimation playButtonAnimation = new PlayButtonAnimation();
    private PlayerViewPagerAdapter playerViewPagerAdapter;
    private final MusicModule musicModule;
    private UserSharedPrefManager userSharedPrefManager;
    private Context context;
    private MusicUtils musicUtils;
    private Boolean musicLoading = false;

    private FragmentMusicPlayerBinding binding;

    public MusicPlayerFragment(MusicModule musicModule) {
        super(R.layout.fragment_music_player);
        this.musicModule = musicModule;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        this.userSharedPrefManager = new UserSharedPrefManager(context);
        this.musics = new ArrayList<>();
        musicUtils = new MusicUtils(context);

        setupService();
        setupPlayerViewPager();
        setupPlayerSeekbar();
        setRepeatAndShuffle();
        initPlayerUi();
        setupViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupPlayerViewPager() {
        playerViewPagerAdapter = new PlayerViewPagerAdapter(context, musics);
        binding.viewPagerExtendedMusicPlayer.setAdapter(playerViewPagerAdapter);

        binding.viewPagerExtendedMusicPlayer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                final Handler handler = new Handler();
                if (position != musicUtils.findCurrentMusicIndex(musics)) {
                    handler.postDelayed(() -> musicModule.getMusicPlayer().playTrack(musics, musics.get(position).getSlug()), 500);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    private void initPlayerUi() {
        setRepeatAndShuffle();
        if (binding == null) {
            return;
        }

        if (musicLoading) {
            binding.fabExtendedMusicPlayerPlay.setVisibility(View.GONE);
            binding.llExtendedMusicPlayerLoading.setVisibility(View.VISIBLE);
            return;
        }

        if (musics.size() == 0) {
            musics.addAll(AppDatabase.getInstance(context).musicDao().getAll());
            playerViewPagerAdapter.notifyDataSetChanged();
        }


        if (!getCurrentSlug().equals("")) {
            if (isPlayerReady()) {
                binding.fabExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
            } else {
                binding.fabExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
            }
            Music music = AppDatabase.getInstance(getActivity()).musicDao().findById(getCurrentSlug());
            if (music != null) {
                binding.tvExtendedMusicPlayerTitle.setText(music.getTitle());
                binding.tvExtendedMusicPlayerArtist.setText(music.getArtist().getName());
                binding.viewPagerExtendedMusicPlayer.setCurrentItem(musicUtils.findCurrentMusicIndex(musics), true);
                binding.fabExtendedMusicPlayerPlay.setVisibility(View.VISIBLE);
                binding.llExtendedMusicPlayerLoading.setVisibility(View.GONE);
            }
        }
    }

    private void setRepeatAndShuffle() {
        switch (userSharedPrefManager.getRepeatMode()) {
            case Constant.REPEAT_MODE_NONE: {
                binding.imgExtendedMusicPlayerRepeat.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_repeat_off));
                break;
            }
            case Constant.REPEAT_MODE_ONE: {
                binding.imgExtendedMusicPlayerRepeat.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_repeat_on));
                break;
            }
        }

        if (userSharedPrefManager.getShuffleMode()) {
            binding.imgExtendedMusicPlayerShuffle.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_shuffle_on));
        } else {
            binding.imgExtendedMusicPlayerShuffle.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_shuffle_off));
        }
    }

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
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
                case Constant.INTENT_START:
                case Constant.INTENT_NEXT:
                case Constant.INTENT_PREVIOUS:
                    resetSeekbar();
                    break;
                case Constant.INTENT_NOTIFY:
                    initPlayerUi();
                case Constant.INTENT_PLAY:
                    binding.fabExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
                    setupSeekbarHandler();
                    break;
                case Constant.INTENT_PAUSE:
                    binding.fabExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
                    break;
            }
        }

//        if (!intent.hasExtra("progress")) {
//        musicLoading = intent.getBooleanExtra(Constant.INTENT_LOADING, false);
//        }
    }

    private void setupPlayerSeekbar() {
        binding.seekbarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                is_seeking = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                is_seeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicModule.getMusicPlayer().seekTo(seekBar.getProgress());
                is_seeking = false;
            }
        });
    }

    private void resetSeekbar() {
        binding.seekbarMusic.setMax(0);
        binding.seekbarMusic.setProgress(0);
    }

    private void setupSeekbarHandler() {
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicModule.getMusicPlayer().isPlayerReady() && !is_seeking) {
                    calculateSeekbar();
                }
                mHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
            }
        });
    }

    private void calculateSeekbar() {
        binding.seekbarMusic.setMax((int) musicModule.getMusicPlayer().getDuration());
        long elapsed = (int) musicModule.getMusicPlayer().getCurrentMusicPosition();
        long remaining = binding.seekbarMusic.getMax();

        long minutes_elapsed = (elapsed / 1000) / 60;
        long seconds_elapsed = ((elapsed / 1000) % 60);

        long minutes_remaining = (remaining / 1000) / 60;
        long seconds_remaining = ((remaining / 1000) % 60);

        String remaining_seconds_string = "" + seconds_remaining;
        String elapsed_seconds_string = "" + seconds_elapsed;
        if (seconds_remaining < 10) {
            remaining_seconds_string = "0" + seconds_remaining;
        }
        if (seconds_elapsed < 10) {
            elapsed_seconds_string = "0" + seconds_elapsed;
        }
        binding.tvExtendedMusicPlayerTime.setText(minutes_elapsed + ":" + elapsed_seconds_string);
        binding.tvExtendedMusicPlayerDuration.setText(minutes_remaining + ":" + remaining_seconds_string);
        binding.seekbarMusic.setProgress((int) musicModule.getMusicPlayer().getCurrentMusicPosition());
    }

    private void setupViews() {
        binding.fabExtendedMusicPlayerPlay.setOnClickListener(view -> play());
        binding.imgExtendedMusicPlayerNext.setOnClickListener(view -> next());
        binding.imgExtendedMusicPlayerPrevious.setOnClickListener(view -> previous());
        binding.imgExtendedMusicPlayerShuffle.setOnClickListener(view -> shuffle());
        binding.imgExtendedMusicPlayerRepeat.setOnClickListener(view -> repeat());
    }

    private void play() {
        String currentSlug = getCurrentSlug();
        if (currentSlug.equals("")) {
            return;
        }
        playButtonAnimation.showBonceAnimation(binding.fabExtendedMusicPlayerPlay);
        new Handler().postDelayed(() -> musicModule.getMusicPlayer().togglePlay(), 400);
    }

    private void previous() {
        musicModule.getMusicPlayer().skip(false);
    }

    private void next() {
        musicModule.getMusicPlayer().skip(true);
    }

    private void repeat() {
        if (userSharedPrefManager.getRepeatMode() == Constant.REPEAT_MODE_NONE)
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_ONE);
        else
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_NONE);

        setRepeatAndShuffle();
    }

    private void shuffle() {
        userSharedPrefManager.setShuffleMode(!userSharedPrefManager.getShuffleMode());
        setRepeatAndShuffle();
    }

    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private boolean isPlayerReady() {
        return musicModule.getMusicPlayer().isPlayerReady();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        musics.clear();
        musics.addAll(event.musics);
        playerViewPagerAdapter.notifyDataSetChanged();
        initPlayerUi();
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

        this.getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_music_player_actions, MusicPlayerActionsFragment.class, null)
                .commit();

        this.getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_music_queue, MusicPlayerQueueFragment.class, null)
                .commit();
    }
}

