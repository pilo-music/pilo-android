package app.pilo.android.adapters;

import android.content.Context;
import android.view.View;

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
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;

public class SearchArtistsAdapter extends ArtistVerticalListAdapter {

    private SearchApi searchApi;
    private int searchId;

    public SearchArtistsAdapter(WeakReference<Context> context, List<Artist> artists, int searchId) {
        super(context, artists);
        this.searchId = searchId;
        searchApi = new SearchApi(context.get());
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistCarouselAdapterViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Artist artist = artists.get(position);
        holder.ll_artist_vertical.setOnClickListener(v -> {
            SingleArtistFragment fragment = new SingleArtistFragment(artist);
            if (context instanceof MainActivity)
                ((MainActivity) context).pushFragment(fragment);
            else {
                MainActivity activity = new MainActivity();
                activity.pushFragment(fragment);
            }
            searchApi.search(searchId, artist.getSlug(), "artist", new HttpHandler.RequestHandler() {
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

