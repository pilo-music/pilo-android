package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.BookmarkApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.utils.MusicDownloader;
import app.pilo.android.utils.Utils;

public class MusicActionsDialog {

    private Music music;
    private Context context;
    private LikeApi likeApi;
    private BookmarkApi bookmarkApi;
    private boolean likeProcess = false;
    private boolean bookmarkProcess = false;
    private Utils utils;
    private Dialog dialog;


    private RoundedImageView riv_music_actions_image;
    private TextView tv_music_actions_music;
    private TextView tv_music_actions_artist;
    private ImageView img_music_actions_like;
    private ImageView img_music_actions_bookmark;
    private LinearLayout ll_music_actions_go_to_artist;
    private LinearLayout ll_music_actions_add_to_playlist;
    private LinearLayout ll_music_actions_download;
    private LinearLayout ll_music_actions_share;
    private ImageView img_music_actions_download;
    private DownloadButtonProgress downloadprogress_music_actions;
    private UserSharedPrefManager userSharedPrefManager;

    public MusicActionsDialog(Context context, Music music) {
        this.context = context;
        this.music = music;
        likeApi = new LikeApi(context);
        bookmarkApi = new BookmarkApi(context);
        utils = new Utils();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.music_actions_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        this.dialog = dialog;
        userSharedPrefManager = new UserSharedPrefManager(context);
        findViews();
        setupViews();

        setupDownload();
        setupLike();
        setupBookmark();
        setupDownload();

        dialog.show();
    }


    private void findViews() {
        riv_music_actions_image = dialog.findViewById(R.id.riv_music_actions_image);
        tv_music_actions_music = dialog.findViewById(R.id.tv_music_actions_music);
        tv_music_actions_artist = dialog.findViewById(R.id.tv_music_actions_artist);
        img_music_actions_like = dialog.findViewById(R.id.img_music_actions_like);
        img_music_actions_bookmark = dialog.findViewById(R.id.img_music_actions_bookmark);
        ll_music_actions_go_to_artist = dialog.findViewById(R.id.ll_music_actions_go_to_artist);
        ll_music_actions_add_to_playlist = dialog.findViewById(R.id.ll_music_actions_add_to_playlist);
        ll_music_actions_download = dialog.findViewById(R.id.ll_music_actions_download);
        ll_music_actions_share = dialog.findViewById(R.id.ll_music_actions_share);
        downloadprogress_music_actions = dialog.findViewById(R.id.downloadprogress_music_actions);
        img_music_actions_download = dialog.findViewById(R.id.img_music_actions_download);
    }

    private void setupBookmark() {

        if (music.isHas_bookmark()) {
            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_on));
        } else {
            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_off));
        }


        img_music_actions_bookmark.setOnClickListener(v -> {
            if (bookmarkProcess)
                return;
            if (!music.isHas_bookmark()) {
                bookmarkProcess = true;
                img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_on));
                bookmarkApi.bookmark(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_off));
                        } else {
                            music.setHas_bookmark(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_off));
                    }
                });
                bookmarkProcess = false;
            } else {
                bookmarkProcess = true;
                img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_off));
                bookmarkApi.bookmark(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_on));
                        } else {
                            music.setHas_bookmark(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_on));
                    }
                });
                bookmarkProcess = false;
            }
        });
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
            dialog.dismiss();
        });

        ll_music_actions_add_to_playlist.setOnClickListener(v -> {
            dialog.dismiss();
            new AddToPlaylistDialog().show();
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
