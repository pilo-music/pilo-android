package app.pilo.android.event;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.activities.MainActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Album;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.models.SinglePlaylist;

public class MusicEvent {

    public final List<Music> musics = new ArrayList<>();

    public MusicEvent(Context context, List<Music> musicListItems, String music_slug, boolean play_when_ready, boolean should_load_related_items) {

        if (musicListItems.size() == 0) {
            return;
        }

        musics.clear();
        musics.addAll(musicListItems);

        AppDatabase.getInstance(context).musicDao().nukeTable();
        AppDatabase.getInstance(context).musicDao().insertAll(musicListItems);

        ((MainActivity) context).play_music(music_slug, play_when_ready, should_load_related_items);
    }
}
