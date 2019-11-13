package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.utils.TypeFace;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleAlbumFragment extends Fragment {
    private View view;
    private String slug, title, image, artist, artist_slug;
    private UserSharedPrefManager sharedPrefManager;

    @BindView(R.id.img_single_album)
    ImageView img_album;
    @BindView(R.id.tv_single_album_name)
    TextView tv_album_name;
    @BindView(R.id.tv_single_album_count)
    TextView tv_album_count;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.tv_single_album_artist)
    TextView tv_album_artist;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.rc_single_album)
    RecyclerView rc_album_musics;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_album, container, false);

        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            slug = getArguments().getString("slug");
            title = getArguments().getString("title");
            artist = getArguments().getString("artist");
            artist_slug = getArguments().getString("artist_slug");
            image = getArguments().getString("image");
        }
        setupViews();
        sharedPrefManager = new UserSharedPrefManager(getActivity());
        getDataFromServer();

        return view;
    }

    private void setupViews() {
        if (image != null && !image.equals("")) {
            Glide.with(getActivity())
                    .load(image)
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_album);
        } else {
            img_album.setImageResource(R.drawable.ic_music_placeholder);
        }
        tv_album_name.setText(title);
        tv_header_title.setText(title);
        img_header_back.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().remove(SingleAlbumFragment.this).commit());

    }

    private void getDataFromServer() {
        AlbumApi albumApi = new AlbumApi(getActivity());
        albumApi.single(slug, new RequestHandler.RequestHandlerWithModel<SingleAlbum>() {
            @Override
            public void onGetInfo(String status, SingleAlbum data) {
                if (status.equals("success") && data != null) {
                    if (data.getAlbum().getMusics() != null) {
                        tv_album_count.setText(data.getAlbum().getMusics().size() + " " + getActivity().getString(R.string.music));
                    } else {
                        tv_album_count.setText("0" + " " + getActivity().getString(R.string.music));
                    }

                    if (sharedPrefManager.getLocal().equals("fa")){
                        tv_album_artist.setTextDirection(View.TEXT_DIRECTION_RTL);
                        tv_album_name.setTextDirection(View.TEXT_DIRECTION_RTL);
                    }


                    tv_album_artist.setText(data.getAlbum().getArtist_name());
                    setupMusic(data.getAlbum().getMusics());

                } else {
                    Alerter.create(getActivity())
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(TypeFace.font(getActivity()))
                            .setTitleTypeface(TypeFace.font(getActivity()))
                            .setButtonTypeface(TypeFace.font(getActivity()))
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }
            }

            @Override
            public void onGetError(VolleyError error) {
                Alerter.create(getActivity())
                        .setTitle(R.string.server_connection_error)
                        .setTextTypeface(TypeFace.font(getActivity()))
                        .setTitleTypeface(TypeFace.font(getActivity()))
                        .setButtonTypeface(TypeFace.font(getActivity()))
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorRes(R.color.colorError)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });
    }

    private void setupMusic(List<Music> musics) {
        if (musics.size() > 0) {
            MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(getActivity(), musics);
            rc_album_musics.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_album_musics.setAdapter(musicVerticalListAdapter);
        }
    }
}
