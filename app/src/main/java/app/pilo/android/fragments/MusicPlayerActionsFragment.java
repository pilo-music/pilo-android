package app.pilo.android.fragments;

import android.content.Context;
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

import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.databinding.FragmentMusicPlayerBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.Utils;

public class MusicPlayerActionsFragment extends Fragment {
    private Context context;
    private LikeApi likeApi;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    private boolean hasDownloadComplete = false;
    private int fileDownloadId = 0;
    private boolean likeProcess = false;

    private FragmentMusicPlayerBinding binding;

    public MusicPlayerActionsFragment() {
        super(R.layout.fragment_music_player_actions);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        userSharedPrefManager = new UserSharedPrefManager(context);
        likeApi = new LikeApi(context);
        utils = new Utils();

        this.getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_music_queue, MusicPlayerQueue.class, null)
                .commit();
        return view;
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

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

}
