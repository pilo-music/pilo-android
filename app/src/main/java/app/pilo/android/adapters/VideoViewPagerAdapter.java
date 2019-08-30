package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Arrays;
import java.util.List;

import app.pilo.android.R;
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

        title.setText(videos.get(position).getTitle());
        artist.setText(videos.get(position).getArtist_name());

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
}