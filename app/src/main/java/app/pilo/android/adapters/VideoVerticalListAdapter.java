package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoVerticalListAdapter extends RecyclerView.Adapter<VideoVerticalListAdapter.videoCarouselAdapterViewHolder> {
    protected Context context;
    protected List<Video> videos;

    public VideoVerticalListAdapter(WeakReference<Context> context, List<Video> videos) {
        this.context = context.get();
        this.videos = videos;
    }

    @NonNull
    @Override
    public videoCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_vertical_list_item, viewGroup, false);
        return new videoCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull videoCarouselAdapterViewHolder holder, final int position) {
        final Video video = videos.get(position);
        holder.tv_video_title.setText(video.getTitle());
        holder.tv_video_artist.setText(video.getArtist().getName());
        Glide.with(context)
                .load(video.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.video_item_image);

        holder.ll_video_vertical.setOnClickListener(v -> {
            SingleVideoFragment mFragment = new SingleVideoFragment(video);
            ((MainActivity) context).pushFragment(mFragment);
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class videoCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_video_vertical_list_item_video)
        TextView tv_video_title;
        @BindView(R.id.tv_video_vertical_list_item_artist)
        TextView tv_video_artist;
        @BindView(R.id.riv_video_vertical_list_item_image)
        ImageView video_item_image;
        @BindView(R.id.ll_video_vertical)
        LinearLayout ll_video_vertical;

        videoCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
