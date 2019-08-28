package app.pilo.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Video;

public class VideoViewPagerAdapter extends PagerAdapter {

    private List<Video> videos;
    private LayoutInflater inflater;
    private Context context;

    public VideoViewPagerAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View myVideoLayout = inflater.inflate(R.layout.video_item, view, false);
        Video video = videos.get(position);
        RoundedImageView imageView = myVideoLayout.findViewById(R.id.riv_video_item_image);
        TextView title = myVideoLayout.findViewById(R.id.tv_video_item_title);
        TextView artist = myVideoLayout.findViewById(R.id.tv_video_item_artist);
        title.setText(video.getTitle());
        artist.setText(video.getArtist_name());
        view.addView(myVideoLayout, 0);
        return myVideoLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}