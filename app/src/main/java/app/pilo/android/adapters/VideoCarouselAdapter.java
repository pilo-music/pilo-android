package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.ref.WeakReference;
import java.util.List;
import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Video;

public class VideoCarouselAdapter extends RecyclerView.Adapter<VideoCarouselAdapter.VideoCarouselAdapterViewHolder> {

    private Context context;
    private List<Video> videos;


    public VideoCarouselAdapter(WeakReference<Context> context, List<Video> videoList) {
        this.context = context.get();
        this.videos = videoList;
    }


    @NonNull
    @Override
    public VideoCarouselAdapter.VideoCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, viewGroup, false);
        return new VideoCarouselAdapter.VideoCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoCarouselAdapterViewHolder viewHolder, int position) {
        Video video = videos.get(position);
        viewHolder.tv_video_item_title.setText(video.getTitle());
        viewHolder.tv_video_item_artist.setText(video.getArtist().getName());
        Glide.with(context)
                .load(video.getImage())
                .placeholder(R.drawable.ic_video_placeholder)
                .error(R.drawable.ic_video_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.riv_video_item_image);
        viewHolder.ll_video_item.setOnClickListener(v -> fragmentJump(video));
    }


    private void fragmentJump(Video video) {
        SingleVideoFragment fragment = new SingleVideoFragment(video);
        ((MainActivity) context).pushFragment(fragment);
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView riv_video_item_image;
        private TextView tv_video_item_title;
        private TextView tv_video_item_artist;
        private LinearLayout ll_video_item;
        private CardView cv_label;

        VideoCarouselAdapterViewHolder(View itemView) {
            super(itemView);
            riv_video_item_image = itemView.findViewById(R.id.riv_video_item_image);
            tv_video_item_title = itemView.findViewById(R.id.tv_video_item_title);
            tv_video_item_artist = itemView.findViewById(R.id.tv_video_item_artist);
            ll_video_item = itemView.findViewById(R.id.ll_video_item);
            cv_label = itemView.findViewById(R.id.cv_label);
        }
    }
}