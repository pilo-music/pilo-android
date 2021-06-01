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
    public String slug;
    public boolean playWhenReady;

    public MusicEvent(Context context, List<Music> musicListItems, String musicSlug, boolean playWhenReady) {
        if (musicListItems.size() == 0) {
            return;
        }

        this.musics = musicListItems;
        this.slug = musicSlug;
        this.playWhenReady = playWhenReady;
    }
}
