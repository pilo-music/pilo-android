package app.pilo.android.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Album;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;

public class Utils {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    public static String getMp3UrlForStreaming(Context context, Music musicTable) {
        String quality = new UserSharedPrefManager(context).getStreamQuality();
        if ("320".equals(quality)) {
            return musicTable.getLink320();
        }
        return musicTable.getLink128();
    }

    public static Typeface font(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/font.ttf");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setStatusColor(Context context,Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static int getScreenWidth(Context context) {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public static int getScreenHeight(Context context) {
        int height;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        height = point.y;
        return height;
    }

    public static void addToPlaylist(Context context, Music music, Playlist playlist) {
//        for (Music item : playlist.getMusics()) {
//            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getSlug(), item.getSlug(), item.getTitle(), item.getImage(), item.getLink128(), item.get(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
//            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
//        }
//
//        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
//        if (currentPlaylistItem != null) {
//            currentPlaylistItem.setIs_playing(true);
//            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
//        }
//
//        EventBus.getDefault().post(new MusicEvent(music,playlist));

    }

    public static void addToPlaylist(Context context, Music music, Album album) {
//        AppDatabase.getInstance(context).currentPlaylistItem().nukeTable();
//        for (Music item : album.getMusics()) {
//            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getId(), item.getSlug(), item.getTitle(), item.getImage(), item.getUrl(), item.getHas_like(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
//            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
//        }
//
//        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
//        if (currentPlaylistItem != null) {
//            currentPlaylistItem.setIs_playing(true);
//            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
//        }
//
//        EventBus.getDefault().post(new MusicEvent(music,album));
    }


    public static void addToPlaylist(Context context, Music music, List<Music> musics) {
//        AppDatabase.getInstance(context).currentPlaylistItem().nukeTable();
//        for (Music item : musics) {
//            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getId(), item.getSlug(), item.getTitle(), item.getImage(), item.getUrl(), item.getHas_like(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
//            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
//        }
//
//        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
//        if (currentPlaylistItem != null) {
//            currentPlaylistItem.setIs_playing(true);
//            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
//        }
//
//        EventBus.getDefault().post(new MusicEvent(music,musics));
    }

    public void animateHeartButton(final View v) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(v, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(v, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();
    }

    public void animatePhotoLike(final View vBgLike, final View ivLike) {
        vBgLike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.VISIBLE);

        vBgLike.setScaleY(0.1f);
        vBgLike.setScaleX(0.1f);
        vBgLike.setAlpha(1f);
        ivLike.setScaleY(0.1f);
        ivLike.setScaleX(0.1f);

        android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();

        android.animation.ObjectAnimator bgScaleYAnim = android.animation.ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        android.animation.ObjectAnimator bgScaleXAnim = android.animation.ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        android.animation.ObjectAnimator bgAlphaAnim = android.animation.ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        android.animation.ObjectAnimator imgScaleUpYAnim = android.animation.ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        android.animation.ObjectAnimator imgScaleUpXAnim = android.animation.ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        android.animation.ObjectAnimator imgScaleDownYAnim = android.animation.ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        android.animation.ObjectAnimator imgScaleDownXAnim = android.animation.ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                vBgLike.setVisibility(View.INVISIBLE);
                ivLike.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.start();
    }

}
