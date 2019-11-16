package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleVideoFragment extends Fragment {
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.img_header_more)
    ImageView img_header_more;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.riv_single_video_image)
    RoundedImageView videoImage;

    private String slug, title, artist, artist_slug, image, url;

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
        return view;
    }

    private void setupViews() {
        tv_header_title.setText(title);
        Glide.with(getActivity())
                .load(image)
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(videoImage);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick(R.id.fl_single_video)
    void playVideo() {
        if (!url.equals("")) {
            Intent mIntent = new Intent(getActivity(), VideoPlayerActivity.class);
            mIntent.putExtra("url", url);
            startActivity(mIntent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
        }
    }

}
