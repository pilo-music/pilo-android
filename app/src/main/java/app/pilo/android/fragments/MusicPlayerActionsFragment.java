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
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.error.VolleyError;
import com.downloader.PRDownloader;
import com.downloader.Progress;
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
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.Utils;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MusicPlayerActionsFragment extends Fragment {
    private Context context;
    private LikeApi likeApi;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    private boolean hasDownloadComplete = false;
    private int fileDownloadId = 0;
    private boolean likeProcess = false;

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
        Music music = AppDatabase.getInstance(getActivity()).musicDao().findById(getCurrentSlug());
        if (music != null) {
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
        }
    }


    void setupViews() {
        binding.imgExtendedMusicPlayerGoToArtist.setOnClickListener(view -> toArtist());
        binding.imgExtendedMusicPlayerShare.setOnClickListener(view -> share());
        binding.imgExtendedMusicPlayerAddToPlaylist.setOnClickListener(view -> addToPlaylist());
        binding.imgExtendedMusicPlayerDownload.setOnClickListener(view -> download());
        binding.downloadProgressExtendedMusicPlayer.setOnClickListener(view -> clickDownloadProgress());
        binding.imgExtendedMusicPlayerLike.setOnClickListener(view -> like());
    }

    void toArtist() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {

            Music music = getCurrentMusic();
            if (music == null)
                return;

            getMainActivity().pushFragment(new SingleArtistFragment(music.getArtist()));
            getMainActivity().getSliding_layout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        }

    }

    void share() {
        //todo conflict with ui
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            Music music = getCurrentMusic();
            if (music == null)
                return;

            new ShareCompat.IntentBuilder(context)
                    .setType("text/plain")
                    .setChooserTitle(music.getTitle())
                    .setText(music.getShare_url())
                    .startChooser();
        }
    }

    void addToPlaylist() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            Music music = getCurrentMusic();
            if (music == null)
                return;

            getMainActivity().pushFragment(new AddToPlaylistFragment(music));
            getSlidingLayout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

    }

    void download() {
        binding.imgExtendedMusicPlayerDownload.setVisibility(View.GONE);
        binding.downloadProgressExtendedMusicPlayer.setVisibility(View.VISIBLE);
        binding.downloadProgressExtendedMusicPlayer.setIndeterminate();
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            Music music = getCurrentMusic();
            if (music == null)
                return;


            hasDownloadComplete = false;
            fileDownloadId = 0;
            fileDownloadId = MusicDownloader.download(context, music, new MusicDownloader.iDownload() {
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

    void clickDownloadProgress() {
        if (!hasDownloadComplete && fileDownloadId != 0) {
            PRDownloader.cancel(fileDownloadId);
            binding.imgExtendedMusicPlayerDownload.setVisibility(View.VISIBLE);
            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
        }
    }

    void like() {
        if (getSlidingLayout().getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            Music music = getCurrentMusic();
            if (music == null)
                return;

            if (likeProcess)
                return;
            if (!music.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(binding.imgExtendedMusicPlayerLike);
                binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getMainActivity(), message);
                            binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                        } else {
                            music.setHas_like(true);
                            AppDatabase.getInstance(context).musicDao().update(music);
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
                likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getMainActivity(), message);
                            binding.imgExtendedMusicPlayerLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                        } else {
                            music.setHas_like(false);
                            AppDatabase.getInstance(context).musicDao().update(music);
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
