package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleMusicFragment extends Fragment {
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.img_header_more)
    ImageView img_header_more;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.riv_single_music)
    RoundedImageView riv_single_music;
    @BindView(R.id.tv_single_music_item_title)
    TextView tv_single_music_item_title;
    @BindView(R.id.tv_single_music_item_artist)
    TextView tv_single_music_item_artist;
    @BindView(R.id.img_single_music_download)
    ImageView img_single_music_download;
    @BindView(R.id.img_single_music_like)
    ImageView img_single_music_like;
    @BindView(R.id.img_single_music_play)
    ImageView img_single_music_play;
    @BindView(R.id.img_single_music_shuffle)
    ImageView img_single_music_shuffle;
    @BindView(R.id.tv_music_vertical_title)
    TextView tv_music_vertical_title;
    @BindView(R.id.rc_music_vertical)
    RecyclerView rc_music_vertical;
    @BindView(R.id.tv_music_carousel_title)
    TextView tv_music_carousel_title;


    private String slug, title, artist, artist_slug, image, url;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_music, container, false);
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
        tv_single_music_item_title.setText(title);
        tv_single_music_item_artist.setText(artist);
        tv_music_vertical_title.setText(getString(R.string.your_playlist));
        tv_music_carousel_title.setText(getString(R.string.music_related));
    }
}
