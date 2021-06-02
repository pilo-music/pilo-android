package app.pilo.android.services.MusicPlayer;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MusicApi;
import app.pilo.android.api.PlayHistoryApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.PlayHistory;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MusicUtils {
    private final Context context;
    private final MusicApi musicApi;
    private final PlayHistoryApi playHistoryApi;
    private final UserSharedPrefManager userSharedPrefManager;

    public MusicUtils(Context context) {
        this.context = context;
        this.musicApi = new MusicApi(context);
        this.playHistoryApi = new PlayHistoryApi(context);
        this.userSharedPrefManager = new UserSharedPrefManager(context);
    }

    public void loadRelativeMusics(List<Music> musicListItems, String musicSlug) {
        sendIntent(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("count", 20);
        params.put("related", musicSlug);
        musicApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    EventBus.getDefault().post(new MusicEvent(context, (List<Music>) data, musicSlug, true));
                } else {
                    EventBus.getDefault().post(new MusicEvent(context, musicListItems, musicSlug, true));
                }

                sendIntent(false);
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                EventBus.getDefault().post(new MusicEvent(context, musicListItems, musicSlug, true));
                sendIntent(false);
            }
        });
    }


    public String getMp3UrlForStreaming(Context context, Music musicTable) {
        String quality = new UserSharedPrefManager(context).getStreamQuality();
        if ("320".equals(quality)) {
            if (!musicTable.getLink320().equals(""))
                return musicTable.getLink320();
            return musicTable.getLink128();
        }

        if (!musicTable.getLink128().equals(""))
            return musicTable.getLink128();
        return musicTable.getLink320();
    }


    public void addMusicsToDB(List<Music> musicList) {
        AppDatabase.getInstance(context).musicDao().nukeTable();
        AppDatabase.getInstance(context).musicDao().insertAll(musicList);
    }


    public void addMusicToHistory(Music music) {
        PlayHistory playHistory = AppDatabase.getInstance(context).playHistoryDao().search(music.getSlug());
        if (playHistory != null) {
            AppDatabase.getInstance(context).playHistoryDao().delete(playHistory);
        }
        playHistory = new PlayHistory();
        playHistory.setMusic(music);
        AppDatabase.getInstance(context).playHistoryDao().insert(playHistory);
        playHistoryApi.add(music.getSlug(), "music");
    }

    public int findCurrentMusicIndex(List<Music> musics) {
        int activeIndex = -1;
        for (int i = 0; i < musics.size(); i++) {
            if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                activeIndex = i;
            }
        }
        return activeIndex;
    }

    public void findDefaultMusic() {
        musicApi.get(null, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    List<Music> result = (List<Music>) data;
                    EventBus.getDefault().post(new MusicEvent(context, result, result.get(0).getSlug(), false));
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
            }
        });
    }


    private void sendIntent(boolean value) {
        Intent intent = new Intent();
        intent.setAction(CUSTOM_PLAYER_INTENT);
        intent.putExtra("loading", value);
        context.sendBroadcast(intent);
    }

}
