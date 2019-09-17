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

import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Video;

public class VideoCarouselAdapter extends SliderViewAdapter<VideoCarouselAdapter.SliderAdapterVH> {

    private Context context;
    private List<Video> videos;


    public VideoCarouselAdapter(Context context, List<Video> videoList) {
        this.context = context;
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
        viewHolder.cv_label.setAlpha(.7f);
        viewHolder.tv_video_item_title.setText(video.getTitle() + " - " + video.getArtist_name());
        Picasso.get()
                .load(video.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
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
        switchContent(mFragment);
    }

    private void switchContent(Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(R.id.framelayout, fragment);
        }
    }


    @Override
    public int getCount() {
        return videos.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private View itemView;
        private RoundedImageView riv_video_item_image;
        private TextView tv_video_item_title;
        private LinearLayout ll_video_item;
        private CardView cv_label;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            riv_video_item_image = itemView.findViewById(R.id.riv_video_item_image);
            tv_video_item_title = itemView.findViewById(R.id.tv_video_item_title);
            ll_video_item = itemView.findViewById(R.id.ll_video_item);
            cv_label = itemView.findViewById(R.id.cv_label);
            this.itemView = itemView;
        }
    }
}