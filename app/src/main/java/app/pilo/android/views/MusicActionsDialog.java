package app.pilo.android.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.Progress;
import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.fragments.AddToPlaylistFragment;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicActionsDialog extends BottomSheetDialogFragment {

    private Music music;
    private Context context;
    private LikeApi likeApi;
    private boolean likeProcess = false;
    private Utils utils;

    public final static String TAG = "MusicActionsDialog";

    private UserSharedPrefManager userSharedPrefManager;


    @BindView(R.id.riv_music_actions_image)
    RoundedImageView riv_music_actions_image;
    @BindView(R.id.tv_music_actions_music)
    TextView tv_music_actions_music;
    @BindView(R.id.tv_music_actions_artist)
    TextView tv_music_actions_artist;
    @BindView(R.id.img_music_actions_like)
    ImageView img_music_actions_like;
    @BindView(R.id.ll_music_actions_go_to_artist)
    LinearLayout ll_music_actions_go_to_artist;
    @BindView(R.id.ll_music_actions_add_to_playlist)
    LinearLayout ll_music_actions_add_to_playlist;
    @BindView(R.id.ll_music_actions_download)
    LinearLayout ll_music_actions_download;
    @BindView(R.id.ll_music_actions_share)
    LinearLayout ll_music_actions_share;
    @BindView(R.id.img_music_actions_download)
    ImageView img_music_actions_download;
    @BindView(R.id.downloadprogress_music_actions)
    DownloadButtonProgress downloadprogress_music_actions;



    public MusicActionsDialog(Context context, Music music) {
        this.context = context;
        this.music = music;
        likeApi = new LikeApi(context);
        utils = new Utils();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_actions_dialog, container, false);
        ButterKnife.bind(this, view);
        userSharedPrefManager = new UserSharedPrefManager(context);

        setupViews();
        setupDownload();
        setupLike();
        setupDownload();

        return  view;
    }

    private void setupLike() {

        if (music.isHas_like()) {
            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
        } else {
            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
        }

        img_music_actions_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!music.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_music_actions_like);
                img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                        } else {
                            music.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                        } else {
                            music.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });
    }

    private void setupDownload() {
        ll_music_actions_download.setOnClickListener(v -> MusicDownloader.download(context, music, new MusicDownloader.iDownload() {
            @Override
            public void onStartOrResumeListener() {
                img_music_actions_download.setVisibility(View.GONE);
                downloadprogress_music_actions.setVisibility(View.VISIBLE);
                downloadprogress_music_actions.setCurrentProgress(0);
            }

            @Override
            public void onProgressListener(Progress progress) {
                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                // Displays the progress bar for the first time.
                downloadprogress_music_actions.setCurrentProgress((int) progressPercent);
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
                ll_music_actions_download.setEnabled(false);
                ll_music_actions_download.setBackgroundColor(Color.parseColor("#f9f9f9"));
                downloadprogress_music_actions.setVisibility(View.GONE);
                img_music_actions_download.setVisibility(View.VISIBLE);
                img_music_actions_download.setImageDrawable(context.getDrawable(R.drawable.ic_checkmark));
            }
        }));
    }

    private void setupViews() {

        if (MusicDownloader.checkExists(context, music, userSharedPrefManager.getDownloadQuality())) {
            ll_music_actions_download.setEnabled(false);
            ll_music_actions_download.setBackgroundColor(Color.parseColor("#f9f9f9"));
            img_music_actions_download.setImageDrawable(context.getDrawable(R.drawable.ic_checkmark));
        }


        ll_music_actions_go_to_artist.setOnClickListener(v -> {
            SingleArtistFragment singleArtistFragment = new SingleArtistFragment(music.getArtist());
            ((MainActivity) context).pushFragment(singleArtistFragment);
            this.dismiss();
        });

        ll_music_actions_add_to_playlist.setOnClickListener(v -> {
            this.dismiss();
            AddToPlaylistFragment addToPlaylistFragment = new AddToPlaylistFragment(music);
            ((MainActivity) context).pushFragment(addToPlaylistFragment);
        });


        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(riv_music_actions_image);

        tv_music_actions_music.setText(music.getTitle());
        tv_music_actions_artist.setText(music.getArtist().getName());

        ll_music_actions_share.setOnClickListener(v -> ShareCompat.IntentBuilder.from(((MainActivity) context))
                .setType("text/plain")
                .setChooserTitle(music.getTitle())
                .setText(music.getShare_url())
                .startChooser());

    }

}
