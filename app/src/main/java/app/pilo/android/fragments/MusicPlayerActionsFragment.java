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
import app.pilo.android.views.MusicActionsDialog;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MusicPlayerActionsFragment extends Fragment {
    private Context context;
    private LikeApi likeApi;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    private boolean hasDownloadComplete = false;
    private int fileDownloadId = 0;
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
            if (MusicDownloader.checkExists(getActivity(), currentMusic, userSharedPrefManager.getDownloadQuality())) {
                binding.imgSync.setEnabled(false);
                binding.imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));
            } else {
                binding.imgSync.setEnabled(true);
                binding.imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_download));
            }

            if (currentMusic.isHas_like()) {
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
            } else {
                binding.imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
            }
        }
    }


    void setupViews() {
        binding.llSync.setOnClickListener(view -> download());
        binding.downloadProgressExtendedMusicPlayer.setOnClickListener(view -> clickDownloadProgress());
        binding.llLike.setOnClickListener(view -> like());
        binding.imgMore.setOnClickListener(view -> new MusicActionsDialog(context, currentMusic).show(((MainActivity) (context)).getSupportFragmentManager(), MusicActionsDialog.TAG));
    }

    void download() {
        if (fileDownloadId != 0) {
            this.clickDownloadProgress();
            return;
        }

        binding.imgSync.setVisibility(View.GONE);
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
                    binding.imgSync.setEnabled(false);
                    binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
                    binding.imgSync.setVisibility(View.VISIBLE);
                    binding.imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));
                }
            });
        }
    }

    void clickDownloadProgress() {
        if (!hasDownloadComplete && fileDownloadId != 0) {
            PRDownloader.cancel(fileDownloadId);
            binding.imgSync.setVisibility(View.VISIBLE);
            binding.downloadProgressExtendedMusicPlayer.setVisibility(View.GONE);
            fileDownloadId = 0;
        }
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
        this.clickDownloadProgress();
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
