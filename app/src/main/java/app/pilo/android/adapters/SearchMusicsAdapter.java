package app.pilo.android.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.models.Music;

public class SearchMusicsAdapter extends MusicVerticalListAdapter {

    private SearchApi searchApi;
    private int searchId;

    public SearchMusicsAdapter(WeakReference<Context> context, List<Music> musics, int searchId) {
        super(context, musics);
        searchApi = new SearchApi(context.get());
        this.searchId = searchId;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicCarouselAdapterViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Music music = musics.get(position);
        holder.ll_music_vertical.setOnClickListener(v -> {
            EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true, false));
            searchApi.search(searchId, music.getSlug(), "music", new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) { }

                @Override
                public void onGetError(@Nullable VolleyError error) { }
            });
        });

    }
}
