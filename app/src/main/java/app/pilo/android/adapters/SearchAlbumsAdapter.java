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
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Music;

public class SearchAlbumsAdapter extends AlbumVerticalListAdapter {

    private SearchApi searchApi;
    private int searchId;

    public SearchAlbumsAdapter(WeakReference<Context> context, List<Album> albums, int searchId) {
        super(context, albums);
        this.searchId = searchId;
        searchApi = new SearchApi(context.get());
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumCarouselAdapterViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Album album = albums.get(position);
        holder.ll_album_vertical.setOnClickListener(v -> {
            SingleAlbumFragment fragment = new SingleAlbumFragment(album);
            ((MainActivity) context).pushFragment(fragment);
            searchApi.search(searchId, album.getSlug(), "album", new HttpHandler.RequestHandler() {
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
