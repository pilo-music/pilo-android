package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.BookmarkApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.utils.Utils;

public class MusicActionsDialog {

    private Music music;
    private Context context;
    private LikeApi likeApi;
    private BookmarkApi bookmarkApi;
    private boolean likeProcess = false;
    private boolean bookmarkProcess = false;
    private Utils utils;

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

        RoundedImageView riv_music_actions_image = dialog.findViewById(R.id.riv_music_actions_image);
        TextView tv_music_actions_music = dialog.findViewById(R.id.tv_music_actions_music);
        TextView tv_music_actions_artist = dialog.findViewById(R.id.tv_music_actions_artist);
        ImageView img_music_actions_like = dialog.findViewById(R.id.img_music_actions_like);
        ImageView img_music_actions_bookmark = dialog.findViewById(R.id.img_music_actions_bookmark);
        LinearLayout ll_music_actions_go_to_artist = dialog.findViewById(R.id.ll_music_actions_go_to_artist);
        LinearLayout ll_music_actions_add_to_playlist = dialog.findViewById(R.id.ll_music_actions_add_to_playlist);
        LinearLayout ll_music_actions_download = dialog.findViewById(R.id.ll_music_actions_download);
        LinearLayout ll_music_actions_share = dialog.findViewById(R.id.ll_music_actions_share);


        ll_music_actions_go_to_artist.setOnClickListener(v -> {
            SingleArtistFragment singleArtistFragment = new SingleArtistFragment(music.getArtist());
            ((MainActivity) context).pushFragment(singleArtistFragment);
            dialog.dismiss();
        });



        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(riv_music_actions_image);

        tv_music_actions_music.setText(music.getTitle());
        tv_music_actions_artist.setText(music.getArtist().getName());


        if (music.isHas_like()) {
            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
        } else {
            img_music_actions_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
        }

        if (music.isHas_bookmark()) {
            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_on));
        } else {
            img_music_actions_bookmark.setImageDrawable(context.getDrawable(R.drawable.ic_bookmark_off));
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


        dialog.show();
    }

}
