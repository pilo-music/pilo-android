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
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.exoplayer2.Player;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import app.pilo.android.R;
import app.pilo.android.adapters.PlayerViewPagerAdapter;
import app.pilo.android.databinding.FragmentMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.services.MusicPlayer.MusicUtils;
import app.pilo.android.utils.Constant;
import app.pilo.android.utils.PlayButtonAnimation;
import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

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
        setPlayerChangeState();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        if (binding == null){
            return;
        }

        if (musics.size() == 0) {
            musics.addAll(AppDatabase.getInstance(context).musicDao().getAll());
            playerViewPagerAdapter.notifyDataSetChanged();
        }


        if (!getCurrentSlug().equals("")) {
            if (isPlayerReady()) {
                binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
            } else {
                binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
            }
            Music music = AppDatabase.getInstance(getActivity()).musicDao().findById(getCurrentSlug());
            if (music != null) {
                binding.tvExtendedMusicPlayerTitle.setText(music.getTitle());
                binding.tvExtendedMusicPlayerArtist.setText(music.getArtist().getName());
                binding.viewPagerExtendedMusicPlayer.setCurrentItem(musicUtils.findCurrentMusicIndex(musics), true);
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

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
                handleIncomingBroadcast(intent);
            }
        }
    };

    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        active = true;
    }


    private void handleIncomingBroadcast(Intent intent) {
        if (binding == null){
            return;
        }

        if (intent.getIntExtra("progress", -100) != -100) {
            if (!is_seeking) {
                binding.seekbarMusic.setMax(intent.getIntExtra("max", 0));
                long elapsed = intent.getIntExtra("progress", 0);
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
                binding.seekbarMusic.setProgress(intent.getIntExtra("progress", 0));
            }
        } else if (intent.getBooleanExtra("play", false)) {
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
        } else if (intent.getBooleanExtra("pause", false)) {
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
        }

        initPlayerUi();

    }


    private void setupViews() {
        binding.imgExtendedMusicPlayerPlay.setOnClickListener(view -> play());
        binding.imgExtendedMusicPlayerNext.setOnClickListener(view -> next());
        binding.imgExtendedMusicPlayerPrevious.setOnClickListener(view -> previous());
        binding.imgExtendedMusicPlayerShuffle.setOnClickListener(view -> shuffle());
        binding.imgExtendedMusicPlayerRepeat.setOnClickListener(view -> repeat());
    }


    void play() {
        String currentSlug = getCurrentSlug();
        if (currentSlug.equals("")) {
            return;
        }
        musicModule.getMusicPlayer().togglePlay();
        playButtonAnimation.showBonceAnimation(binding.imgExtendedMusicPlayerPlay);
    }

    void previous() {
        if (((musicModule.getMusicPlayer().getCurrentMusicPosition() * 100) / musicModule.getMusicPlayer().getDuration() > 5)) {
            musicModule.getMusicPlayer().seekTo(0);
        } else {
            musicModule.getMusicPlayer().skip(false);
        }
    }

    void next() {
        musicModule.getMusicPlayer().skip(true);
    }

    void repeat() {
        if (userSharedPrefManager.getRepeatMode() == Constant.REPEAT_MODE_NONE)
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_ONE);
        else
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_NONE);

        setRepeatAndShuffle();
    }

    void shuffle() {
        userSharedPrefManager.setShuffleMode(!userSharedPrefManager.getShuffleMode());
        setRepeatAndShuffle();
    }


    void setPlayerChangeState(){
        musicModule.getMusicPlayer().getExoPlayer().addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
