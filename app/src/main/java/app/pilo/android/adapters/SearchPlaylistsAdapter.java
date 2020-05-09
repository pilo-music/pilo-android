package app.pilo.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.fragments.SinglePlaylistFragment;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;

public class SearchPlaylistsAdapter extends PlaylistVerticalListAdapter {

    private SearchApi searchApi;
    private int searchId;

    public SearchPlaylistsAdapter(WeakReference<Context> context, List<Playlist> playlists, int searchId) {
        super(context, playlists);
        this.searchId = searchId;
        searchApi = new SearchApi(context.get());
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistCarouselAdapterViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.ll_playlist_vertical.setOnClickListener(v -> {
            final Playlist playlist = playlists.get(position);
            SinglePlaylistFragment fragment = new SinglePlaylistFragment(playlist);
            ((MainActivity) context).pushFragment(fragment);
            searchApi.search(searchId, playlist.getSlug(), "playlist", new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                }
            });
        });
    }
}
