package app.pilo.android.event;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;

import java.util.ArrayList;
import java.util.List;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Music;

public class MusicEvent {
    public List<Music> musics = new ArrayList<>();

    public MusicEvent(Context context, List<Music> musicListItems, String musicSlug, boolean playWhenReady) {
        if (musicListItems.size() == 0) {
            return;
        }

        playMusics(context, musicSlug, playWhenReady);
    }

    private void playMusics(Context context, String musicSlug, boolean playWhenReady) {
        ((MainActivity) context).play_music(musicSlug, playWhenReady);
    }
}
