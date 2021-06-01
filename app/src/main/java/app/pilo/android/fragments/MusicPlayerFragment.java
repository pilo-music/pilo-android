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
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.PlayerViewPagerAdapter;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.MusicApi;
import app.pilo.android.api.PlayHistoryApi;
import app.pilo.android.databinding.FragmentMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicModule;
import app.pilo.android.services.PlayerService;
import app.pilo.android.utils.Constant;
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.PlayButtonAnimation;
import app.pilo.android.utils.Utils;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MusicPlayerFragment extends Fragment {

//    // extended music player
//    @BindView(R.id.tv_extended_music_player_title)
//    TextView tv_extended_music_player_title;
//    @BindView(R.id.tv_extended_music_player_artist)
//    TextView tv_extended_music_player_artist;
//    @BindView(R.id.seekbar_music)
//    SeekBar player_progress;
//    @BindView(R.id.img_extended_music_player_play)
//    FloatingActionButton img_extended_music_player_play;
//    @BindView(R.id.img_extended_music_player_next)
//    ImageView img_extended_music_player_next;
//    @BindView(R.id.rc_music_vertical)
//    RecyclerView rc_music_vertical;
//    @BindView(R.id.tv_extended_music_player_time)
//    TextView tv_extended_music_player_time;
//    @BindView(R.id.tv_extended_music_player_duration)
//    TextView tv_extended_music_player_duration;
//    @BindView(R.id.img_extended_music_player_shuffle)
//    ImageView img_extended_music_player_shuffle;
//    @BindView(R.id.img_extended_music_player_repeat)
//    ImageView img_extended_music_player_repeat;
//    @BindView(R.id.img_extended_music_player_download)
//    ImageView img_extended_music_player_download;
//    @BindView(R.id.download_progress_extended_music_player)
//    DownloadButtonProgress download_progress_extended_music_player;
//    @BindView(R.id.img_extended_music_player_like)
//    ImageView img_extended_music_player_like;
//    @BindView(R.id.ll_music_vertical_show_more)
//    LinearLayout ll_music_vertical_show_more;
//    @BindView(R.id.tv_music_vertical_title)
//    TextView tv_music_vertical_title;
//    @BindView(R.id.view_pager_extended_music_player)
//    ViewPager2 view_pager_extended_music_player;
//    @BindView(R.id.ll_extended_music_player_controls)
//    LinearLayout ll_extended_music_player_controls;
//    @BindView(R.id.ll_extended_music_player_loading)
//    LinearLayout ll_extended_music_player_loading;

    private boolean musicLoading = false;
    private boolean is_seeking;
    private final Handler mHandler = new Handler();
    private boolean hasDownloadComplete = false;
    private int fileDownloadId = 0;
    private boolean likeProcess = false;
    private boolean active = false;

    private List<Music> musics;
    private LikeApi likeApi;
    private Utils utils;
    private final PlayButtonAnimation playButtonAnimation = new PlayButtonAnimation();
    private PlayHistoryApi playHistoryApi;
    private PlayerViewPagerAdapter playerViewPagerAdapter;
    private MusicApi musicApi;
    private MusicModule musicModule;
    private UserSharedPrefManager userSharedPrefManager;

    private FragmentMusicPlayerBinding binding;

    public MusicPlayerFragment() {
        super(R.layout.fragment_music_player);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        this.userSharedPrefManager = new UserSharedPrefManager(getActivity());
        this.musics = new ArrayList<>();

        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        getActivity().registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        active = true;

        Intent player_service_intent = new Intent(getActivity(), PlayerService.class);
        getActivity().bindService(player_service_intent, mConnection, getActivity().BIND_AUTO_CREATE);

        setupPlayerViewPager();
        setupPlayerSeekbar();
        setRepeatAndShuffle();

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
//                if (playerService != null) {
                musicModule.getMusicPlayer().seekTo(seekBar.getProgress());
//                }

                is_seeking = false;
            }
        });
    }

    private void setupPlayerViewPager() {
        playerViewPagerAdapter = new PlayerViewPagerAdapter(getActivity(), musics);
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
                if (position != musicModule.getMusicPlayer().findCurrentMusicIndex(musics)) {
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
//        checkPlayerService();
//        if (playerService == null) {
//            return;
//        }
        setRepeatAndShuffle();
//        if (musics.size() == 0) {
//            List<Music> items_from_db = AppDatabase.getInstance(MainActivity.this).musicDao().getAll();
//            musics.addAll(items_from_db);
//            musicVerticalListAdapter.notifyDataSetChanged();
//            playerViewPagerAdapter.notifyDataSetChanged();
//            view_pager_extended_music_player.setCurrentItem(getCurrentMusicIndex(), true);
//        }

        if (!getCurrentSlug().equals("")) {

//            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
//                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            }

            if (isPlayerReady()) {
                binding.imgExtendedMusicPlayerPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_icon));
//                img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_pause_icon));
            } else {
                binding.imgExtendedMusicPlayerPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play_icon));
//                img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_play_icon));
            }
            Music music = AppDatabase.getInstance(getActivity()).musicDao().findById(getCurrentSlug());
            if (music != null) {
//                tv_music_player_collapsed_title.setText(music.getTitle());
                binding.tvExtendedMusicPlayerTitle.setText(music.getTitle());
                binding.tvExtendedMusicPlayerArtist.setText(music.getArtist().getName());
//                tv_music_player_collapsed_artist.setText(music.getArtist().getName());

//                Glide.with(getActivity())
//                        .load(music.getImage())
//                        .placeholder(R.drawable.ic_music_placeholder)
//                        .error(R.drawable.ic_music_placeholder)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(riv_music_player_collapsed_image);

                if (MusicDownloader.checkExists(getActivity(), music, userSharedPrefManager.getDownloadQuality())) {
                    binding.imgExtendedMusicPlayerDownload.setEnabled(false);
                    binding.imgExtendedMusicPlayerDownload.setImageDrawable(getActivity().getDrawable(R.drawable.ic_checkmark));
                } else {
                    binding.imgExtendedMusicPlayerDownload.setEnabled(true);
                    binding.imgExtendedMusicPlayerDownload.setImageDrawable(getActivity().getDrawable(R.drawable.ic_download));
                }

                if (music.isHas_like()) {
                    binding.imgExtendedMusicPlayerLike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                } else {
                    binding.imgExtendedMusicPlayerLike.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                }

                binding.viewPagerExtendedMusicPlayer.setCurrentItem(musicModule.getMusicPlayer().findCurrentMusicIndex(musics), true);
                // add play history
//                addMusicToHistory(music);
            }

        } else {
//            checkActiveMusic();
        }
    }

    private void setRepeatAndShuffle() {
        switch (userSharedPrefManager.getRepeatMode()) {
            case Constant.REPEAT_MODE_NONE: {
                binding.imgExtendedMusicPlayerRepeat.setImageDrawable(getActivity().getDrawable(R.drawable.ic_repeat_off));
                break;
            }
            case Constant.REPEAT_MODE_ONE: {
                binding.imgExtendedMusicPlayerRepeat.setImageDrawable(getActivity().getDrawable(R.drawable.ic_repeat_on));
                break;
            }
        }

        if (userSharedPrefManager.getShuffleMode()) {
            binding.imgExtendedMusicPlayerShuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_on));
        } else {
            binding.imgExtendedMusicPlayerShuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_off));
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


    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder mLocalBinder = (PlayerService.LocalBinder) service;
            PlayerService playerService = mLocalBinder.getServerInstance();
            if (playerService != null && active) {
                musicModule = playerService.getMusicModule();
                initPlayerUi();
            }
        }
    };


    private void handleIncomingBroadcast(Intent intent) {
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
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_icon));
//            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_pause_icon));
        } else if (intent.getBooleanExtra("pause", false)) {
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play_icon));
//            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_play_icon));
//            if (playerService != null) {
            musicModule.getMusicPlayer().getExoPlayer().setPlayWhenReady(false);
//            }
        } else if (intent.getBooleanExtra("notify", false)) {
            initPlayerUi();
        } else if (intent.getBooleanExtra("close", false)) {
            if (active) {
                getActivity().finish();
            }
        } else if (intent.getBooleanExtra("ended", false)) {
            switch (userSharedPrefManager.getRepeatMode()) {
                case Constant.REPEAT_MODE_NONE: {
                    binding.imgExtendedMusicPlayerNext.callOnClick();
                    break;
                }
                case Constant.REPEAT_MODE_ONE: {
                    musicModule.getMusicPlayer().playTrack(musics, getCurrentSlug());
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        musics.clear();
        musics.addAll(event.musics);
        playerViewPagerAdapter.notifyDataSetChanged();
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


    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private boolean isPlayerReady() {
        return musicModule.getMusicPlayer().isPlayerReady();
    }

}
