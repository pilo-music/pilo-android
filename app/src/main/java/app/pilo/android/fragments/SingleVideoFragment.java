package app.pilo.android.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.pilo.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleVideoFragment extends Fragment  {
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.img_header_more)
    ImageView img_header_more;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.ep_video_view)
    PlayerView videoView;

    private String slug, title, artist, artist_slug, image, url;
    private ExoPlayer exoPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_video, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            slug = getArguments().getString("slug");
            title = getArguments().getString("title");
            artist = getArguments().getString("artist");
            artist_slug = getArguments().getString("artist_slug");
            image = getArguments().getString("image");
            url = getArguments().getString("url");
        }
        setupViews();
        setupVideoPlayer();
        return view;
    }

    private void setupVideoPlayer() {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        DefaultLoadControl loadControl = new DefaultLoadControl();
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
    }

    private void setupViews() {
        tv_header_title.setText(title);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
