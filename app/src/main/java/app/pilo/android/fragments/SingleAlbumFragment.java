package app.pilo.android.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.PlayHistoryApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Album;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleAlbum;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleAlbumFragment extends BaseFragment {
    private View view;
    private UserSharedPrefManager sharedPrefManager;
    private Utils utils;
    private LikeApi likeApi;
    private boolean likeProcess = false;
    private SingleAlbum singleAlbum;
    private Album album;
    private MusicVerticalListAdapter musicVerticalListAdapter;
    private UserSharedPrefManager userSharedPrefManager;
    private ItemTouchHelper itemTouchHelper;


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
    @BindView(R.id.fab_single_album_shuffle)
    FloatingActionButton fab_single_album_shuffle;
    @BindView(R.id.fab_single_album_play)
    FloatingActionButton fab_single_album_play;


    @BindView(R.id.rc_album_carousel)
    RecyclerView rc_album_carousel;
    @BindView(R.id.tv_album_carousel_title)
    TextView tv_album_carousel_title;
    @BindView(R.id.sfl_album)
    ShimmerFrameLayout sfl_album;

    public SingleAlbumFragment(Album album) {
        this.album = album;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_album, container, false);

        ButterKnife.bind(this, view);
        userSharedPrefManager = new UserSharedPrefManager(getActivity());
        utils = new Utils();
        likeApi = new LikeApi(getActivity());
        setupViews();
        sharedPrefManager = new UserSharedPrefManager(getActivity());
        getDataFromServer();
        return view;
    }

    private void setupViews() {
        if (album.getImage() != null && !album.getImage().equals("")) {
            Glide.with(getActivity())
                    .load(album.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_album);
        } else {
            img_album.setImageResource(R.drawable.ic_music_placeholder);
        }
        tv_album_name.setText(album.getTitle());
        tv_header_title.setText(album.getTitle());
        tv_album_carousel_title.setText(getString(R.string.related));
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        if (userSharedPrefManager.getShuffleMode()) {
            fab_single_album_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_single_album_shuffle.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
        } else {
            fab_single_album_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon));
            fab_single_album_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        }

    }

    private void getDataFromServer() {
        AlbumApi albumApi = new AlbumApi(getActivity());
        albumApi.single(album.getSlug(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                if (status) {
                    if (((SingleAlbum) data).getMusics() != null) {
                        tv_album_count.setText(((SingleAlbum) data).getMusics().size() + " " + getResources().getString(R.string.music));
                    } else {
                        tv_album_count.setText("0" + " " + getResources().getString(R.string.music));
                    }

                    tv_album_artist.setText(((SingleAlbum) data).getAlbum().getArtist().getName());
                    setupMusic(((SingleAlbum) data).getMusics());
                    singleAlbum = ((SingleAlbum) data);

                    if (rc_album_carousel != null) {
                        sfl_album.setVisibility(View.GONE);
                        rc_album_carousel.setVisibility(View.VISIBLE);
                        AlbumsListAdapter albumCarouselAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), singleAlbum.getRelated());
                        rc_album_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                        rc_album_carousel.setAdapter(albumCarouselAdapter);
                    }

                    setupLikeButton();
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }

            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (!checkView()) {
                    return;
                }
                new HttpErrorHandler(getActivity());
            }
        });
    }

    private void setupLikeButton() {
        if (singleAlbum == null || view != null) {
            return;
        }

        if (singleAlbum.isHas_like()) {
            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
        } else {
            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
        }
        img_single_album_like.setVisibility(View.VISIBLE);


        img_single_album_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!singleAlbum.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_single_album_like);
                img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                likeApi.like(album.getSlug(), "album", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!checkView()) {
                            return;
                        }
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                        } else {
                            singleAlbum.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        if (!checkView()) {
                            return;
                        }
                        new HttpErrorHandler(getActivity());
                        img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_single_album_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                likeApi.like(album.getSlug(), "album", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!checkView()) {
                            return;
                        }
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_album_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_on));
                        } else {
                            singleAlbum.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        if (!checkView()) {
                            return;
                        }
                        new HttpErrorHandler(getActivity());
                        img_single_album_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_on));

                    }
                });
                likeProcess = false;
            }
        });

    }


    private void setupMusic(List<Music> musics) {
        if (musics.size() > 0) {
            musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
            rc_album_musics.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_album_musics.setAdapter(musicVerticalListAdapter);
        }
    }

    @OnClick(R.id.fab_single_album_play)
    void fab_single_album_play() {
        PlayHistoryApi playHistoryApi = new PlayHistoryApi(getActivity());
        playHistoryApi.add(album.getSlug(), "album");
        EventBus.getDefault().post(new MusicEvent(getActivity(), singleAlbum.getMusics(), singleAlbum.getMusics().get(0).getSlug(), true, false));

    }

    @OnClick(R.id.fab_single_album_shuffle)
    void fab_single_album_shuffle() {
        if (userSharedPrefManager.getShuffleMode()) {
            userSharedPrefManager.setShuffleMode(false);
            fab_single_album_shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_icon));
            fab_single_album_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        } else {
            userSharedPrefManager.setShuffleMode(true);
            fab_single_album_shuffle.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_single_album_shuffle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        }
    }


    @OnClick(R.id.tv_album_carousel_show_more)
    void tv_album_carousel_show_more() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("related", album.getSlug());
        AlbumsFragment mFragment = new AlbumsFragment(params);
        ((MainActivity) getActivity()).pushFragment(mFragment);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        if (musicVerticalListAdapter != null)
            musicVerticalListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
