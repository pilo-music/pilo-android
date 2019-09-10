package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Video;

public class VideoViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Video> videos;

    public VideoViewPagerAdapter(Context context, List<Video> videos) {
        this.videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View item = LayoutInflater.from(container.getContext()).inflate(R.layout.video_item, container, false);
        TextView title = item.findViewById(R.id.tv_video_item_title);
        TextView artist = item.findViewById(R.id.tv_video_item_artist);
        Video video = videos.get(position);

        title.setText(video.getTitle());
        artist.setText(video.getArtist_name());

        item.setOnClickListener(v -> fragmentJump(video));


        container.addView(item);
        return item;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
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

}