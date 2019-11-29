package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Video;

public class VideoCarouselAdapter extends SliderViewAdapter<VideoCarouselAdapter.SliderAdapterVH> {

    private Context context;
    private List<Video> videos;


    public VideoCarouselAdapter(WeakReference<Context> context, List<Video> videoList) {
        this.context = context.get();
        this.videos = videoList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
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
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", video.getSlug());
        mBundle.putString("title", video.getTitle());
        mBundle.putString("artist", video.getArtist_name());
        mBundle.putString("artist_slug", video.getArtist_slug());
        mBundle.putString("image", video.getImage());
        mBundle.putString("url", video.getUrl());
//        new FragmentSwitcher(context, new SingleVideoFragment(), mBundle);
    }


    @Override
    public int getCount() {
        return videos.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        private RoundedImageView riv_video_item_image;
        private TextView tv_video_item_title;
        private LinearLayout ll_video_item;
        private CardView cv_label;

        SliderAdapterVH(View itemView) {
            super(itemView);
            riv_video_item_image = itemView.findViewById(R.id.riv_video_item_image);
            tv_video_item_title = itemView.findViewById(R.id.tv_video_item_title);
            ll_video_item = itemView.findViewById(R.id.ll_video_item);
            cv_label = itemView.findViewById(R.id.cv_label);
        }
    }
}