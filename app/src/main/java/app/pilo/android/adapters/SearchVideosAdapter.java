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
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;

public class SearchVideosAdapter extends VideoVerticalListAdapter {

    private SearchApi searchApi;
    private int searchId;


    public SearchVideosAdapter(WeakReference<Context> context, List<Video> videos, int searchId) {
        super(context, videos);
        this.searchId = searchId;
        searchApi = new SearchApi(context.get());
    }

    @Override
    public void onBindViewHolder(@NonNull videoCarouselAdapterViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.ll_video_vertical.setOnClickListener(v -> {
            final Video video = videos.get(position);
            SingleVideoFragment mFragment = new SingleVideoFragment(video);
            ((MainActivity) context).pushFragment(mFragment);
            searchApi.search(searchId, video.getSlug(), "video", new HttpHandler.RequestHandler() {
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
