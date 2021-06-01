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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.error.VolleyError;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.PlayerViewPagerAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.databinding.FragmentMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.db.MusicDao;
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
    private PlayerViewPagerAdapter playerViewPagerAdapter;
    private MusicModule musicModule;
    private UserSharedPrefManager userSharedPrefManager;
    private Context context;

    private FragmentMusicPlayerBinding binding;

    public MusicPlayerFragment() {
        super(R.layout.fragment_music_player);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        this.userSharedPrefManager = new UserSharedPrefManager(context);
        this.musics = new ArrayList<>();
//        this.musics = AppDatabase.getInstance(context).musicDao().getAll();
        likeApi = new LikeApi(context);
        utils = new Utils();

        setupService();


        this.getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_music_queue, MusicPlayerQueue.class, null)
                .commit();

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
        setRepeatAndShuffle();
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

                if (MusicDownloader.checkExists(getActivity(), music, userSharedPrefManager.getDownloadQuality())) {
                    binding.imgExtendedMusicPlayerDownload.setEnabled(false);
                    binding.imgExtendedMusicPlayerDownload.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));
                } else {
                    binding.imgExtendedMusicPlayerDownload.setEnabled(true);
                    binding.imgExtendedMusicPlayerDownload.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_download));
                }

                if (music.isHas_like()) {
                    binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                } else {
                    binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                }

                binding.viewPagerExtendedMusicPlayer.setCurrentItem(musicModule.getMusicPlayer().findCurrentMusicIndex(musics), true);
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

        Intent player_service_intent = new Intent(context, PlayerService.class);
        context.bindService(player_service_intent, mConnection, Context.BIND_AUTO_CREATE);

    }


    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder mLocalBinder = (PlayerService.LocalBinder) service;
            PlayerService playerService = mLocalBinder.getServerInstance();
            if (playerService != null && active) {
                musicModule = playerService.getMusicModule();

                setupPlayerViewPager();
                setupPlayerSeekbar();
                setRepeatAndShuffle();
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
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pause_icon));
        } else if (intent.getBooleanExtra("pause", false)) {
            binding.imgExtendedMusicPlayerPlay.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_play_icon));
        }

        initPlayerUi();

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
    }


    void play() {
        String currentSlug = getCurrentSlug();
        if (currentSlug.equals("")) {
            return;
        }
        musicModule.getMusicPlayer().togglePlay();
        playButtonAnimation.showBonceAnimation(binding.viewPagerExtendedMusicPlayer);
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


    void toArtist() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    getMainActivity().pushFragment(new SingleArtistFragment(musics.get(i).getArtist()));
                    getMainActivity().getSliding_layout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        }
    }

    void share() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    new ShareCompat.IntentBuilder(context)
                            .setType("text/plain")
                            .setChooserTitle(musics.get(i).getTitle())
                            .setText(musics.get(i).getShare_url())
                            .startChooser();

                }
            }
        }
    }

    void addToPlaylist() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    getMainActivity().pushFragment(new AddToPlaylistFragment(musics.get(i)));
                    getSlidingLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        }
    }

    void download() {
        binding.imgExtendedMusicPlayerDownload.setVisibility(View.GONE);
        binding.downloadProgressExtendedMusicPlayer.setVisibility(View.VISIBLE);
        binding.downloadProgressExtendedMusicPlayer.setIndeterminate();
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    hasDownloadComplete = false;
                    fileDownloadId = 0;
                    fileDownloadId = MusicDownloader.download(context, musics.get(i), new MusicDownloader.iDownload() {
                        @Override
                        public void onStartOrResumeListener() {
                            binding.downloadProgressExtendedMusicPlayer.setDeterminate();
                            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
                            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.VISIBLE);
                            binding.downloadProgressExtendedMusicPlayer.setCurrentProgress(0);
                        }

                        @Override
                        public void onProgressListener(Progress progress) {
                            long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                            // Displays the progress bar for the first time.
                            binding.downloadProgressExtendedMusicPlayer.setCurrentProgress((int) progressPercent);
                        }

                        @Override
                        public void onPauseListener() {

                        }

                        @Override
                        public void onCancelListener() {

                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onComplete() {
                            hasDownloadComplete = true;
                            binding.imgExtendedMusicPlayerDownload.setEnabled(false);
                            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
                            binding.imgExtendedMusicPlayerDownload.setVisibility(View.VISIBLE);
                            binding.imgExtendedMusicPlayerDownload.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));
                        }
                    });
                }
            }
        }
    }

    void clickDownloadProgress() {
        if (!hasDownloadComplete && fileDownloadId != 0) {
            PRDownloader.cancel(fileDownloadId);
            binding.imgExtendedMusicPlayerDownload.setVisibility(View.VISIBLE);
            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
        }
    }

    void like() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    if (likeProcess)
                        return;
                    if (!musics.get(i).isHas_like()) {
                        likeProcess = true;
                        utils.animateHeartButton(binding.imgExtendedMusicPlayerLike);
                        binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                        int finalI = i;
                        likeApi.like(musics.get(i).getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                            @Override
                            public void onGetInfo(Object data, String message, boolean status) {
                                if (!status) {
                                    new HttpErrorHandler(getMainActivity(), message);
                                    binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                                } else {
                                    musics.get(finalI).setHas_like(true);
                                }
                            }

                            @Override
                            public void onGetError(@Nullable VolleyError error) {
                                new HttpErrorHandler(getMainActivity());
                                binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                            }
                        });
                        likeProcess = false;
                    } else {
                        likeProcess = true;
                        binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                        int finalI1 = i;
                        likeApi.like(musics.get(i).getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                            @Override
                            public void onGetInfo(Object data, String message, boolean status) {
                                if (!status) {
                                    new HttpErrorHandler(getMainActivity(), message);
                                    binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                                } else {
                                    musics.get(finalI1).setHas_like(false);
                                }
                            }

                            @Override
                            public void onGetError(@Nullable VolleyError error) {
                                new HttpErrorHandler(getMainActivity());
                                binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                            }
                        });
                        likeProcess = false;
                    }
                }
            }
        }
    }

    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private boolean isPlayerReady() {
        return musicModule.getMusicPlayer().isPlayerReady();
    }

    private SlidingUpPanelLayout getSlidingLayout() {
        return ((MainActivity) context).getSliding_layout();
    }

    private MainActivity getMainActivity() {
        return ((MainActivity) context);
    }
}
