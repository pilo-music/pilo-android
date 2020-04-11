package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleAlbumFragment extends BaseFragment {
    private View view;
    private String slug, title, image, artist, artist_slug;
    private UserSharedPrefManager sharedPrefManager;
    private SingleAlbum album;
    private Utils utils;
    private LikeApi likeApi;
    private boolean likeProcess = false;

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
    @BindView(R.id.img_single_album_like)
    ImageView img_single_album_like;


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
        utils = new Utils();
        likeApi = new LikeApi(getActivity());
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
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

    }

    private void getDataFromServer() {
        AlbumApi albumApi = new AlbumApi(getActivity());
        albumApi.single(slug, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    if (((SingleAlbum) data).getMusics() != null) {
                        tv_album_count.setText(((SingleAlbum) data).getMusics().size() + " " + getActivity().getString(R.string.music));
                    } else {
                        tv_album_count.setText("0" + " " + getActivity().getString(R.string.music));
                    }

                    if (sharedPrefManager.getLocal().equals("fa")) {
                        tv_album_artist.setTextDirection(View.TEXT_DIRECTION_RTL);
                        tv_album_name.setTextDirection(View.TEXT_DIRECTION_RTL);
                    }


                    tv_album_artist.setText(((SingleAlbum) data).getAlbum().getArtist().getName());
                    setupMusic(((SingleAlbum) data).getMusics());
                    album = ((SingleAlbum) data);
                    setupLikeButton();
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                new HttpErrorHandler(getActivity());
            }
        });
    }

    private void setupLikeButton() {
        if (album == null) {
            return;
        }

        if (album.isHas_like()) {
            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
        } else {
            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
        }
        img_single_album_like.setVisibility(View.VISIBLE);


        img_single_album_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!album.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_single_album_like);
                img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                likeApi.like(album.getAlbum().getSlug(), "album", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                        } else {
                            album.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                likeApi.like(album.getAlbum().getSlug(), "album", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                        } else {
                            album.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });

    }


    private void setupMusic(List<Music> musics) {
        if (musics.size() > 0) {
            MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
            rc_album_musics.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_album_musics.setAdapter(musicVerticalListAdapter);
        }
    }
}
