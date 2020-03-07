package app.pilo.android.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Album;
import app.pilo.android.models.CurrentPlaylistItem;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;

public class Utils {

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
        for (Music item : playlist.getMusics()) {
            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getId(), item.getSlug(), item.getTitle(), item.getImage(), item.getUrl(), item.getHas_like(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
        }

        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
        if (currentPlaylistItem != null) {
            currentPlaylistItem.setIs_playing(true);
            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
        }

        EventBus.getDefault().post(new MusicEvent(music,playlist));

    }

    public static void addToPlaylist(Context context, Music music, Album album) {
        AppDatabase.getInstance(context).currentPlaylistItem().nukeTable();
        for (Music item : album.getMusics()) {
            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getId(), item.getSlug(), item.getTitle(), item.getImage(), item.getUrl(), item.getHas_like(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
        }

        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
        if (currentPlaylistItem != null) {
            currentPlaylistItem.setIs_playing(true);
            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
        }

        EventBus.getDefault().post(new MusicEvent(music,album));
    }


    public static void addToPlaylist(Context context, Music music, List<Music> musics) {
        AppDatabase.getInstance(context).currentPlaylistItem().nukeTable();
        for (Music item : musics) {
            CurrentPlaylistItem playlistItem = new CurrentPlaylistItem(item.getId(), item.getSlug(), item.getTitle(), item.getImage(), item.getUrl(), item.getHas_like(), item.getHas_bookmark(), item.getArtist_id(), item.getArtist_name(), item.getArtist_slug(), false);
            AppDatabase.getInstance(context).currentPlaylistItem().insert(playlistItem);
        }

        CurrentPlaylistItem currentPlaylistItem = AppDatabase.getInstance(context).currentPlaylistItem().findById(music.getId());
        if (currentPlaylistItem != null) {
            currentPlaylistItem.setIs_playing(true);
            AppDatabase.getInstance(context).currentPlaylistItem().update(currentPlaylistItem);
        }

        EventBus.getDefault().post(new MusicEvent(music,musics));
    }
}
