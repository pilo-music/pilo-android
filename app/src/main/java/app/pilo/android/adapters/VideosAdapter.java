package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoAdapterViewHolder> {

    private Context context;
    private List<Video> videos;


    public VideosAdapter(WeakReference<Context> context, List<Video> videoList) {
        this.context = context.get();
        this.videos = videoList;
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, viewGroup, false);
        return new VideosAdapter.VideoAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VideosAdapter.VideoAdapterViewHolder viewHolder, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16);
        viewHolder.ll_video_item.setLayoutParams(params);


        Video video = videos.get(position);

        viewHolder.tv_video_item_title.setText(video.getTitle() + " - " + video.getArtist_name());
        Glide.with(context)
                .load(video.getImage())
                .placeholder(R.drawable.ic_video_placeholder)
                .error(R.drawable.ic_video_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.riv_video_item_image);
        viewHolder.ll_video_item.setOnClickListener(v -> fragmentJump(video));
    }


    private void fragmentJump(Video video) {
        SingleVideoFragment mFragment = new SingleVideoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", video.getSlug());
        mBundle.putString("title", video.getTitle());
        mBundle.putString("artist", video.getArtist_name());
        mBundle.putString("artist_slug", video.getArtist_slug());
        mBundle.putString("image", video.getImage());
        mBundle.putString("url", video.getUrl());
        mFragment.setArguments(mBundle);
        SingleVideoFragment fragment = new SingleVideoFragment();
        fragment.setArguments(mBundle);
        ((MainActivity) context).pushFragment(fragment);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    class VideoAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.riv_video_item_image)
        RoundedImageView riv_video_item_image;
        @BindView(R.id.tv_video_item_title)
        TextView tv_video_item_title;
        @BindView(R.id.ll_video_item)
        LinearLayout ll_video_item;

        VideoAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}