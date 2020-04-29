package app.pilo.android.fragments;

import android.graphics.Color;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.PlaylistApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.SinglePlaylist;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SinglePlaylistFragment extends BaseFragment {
    private View view;
    private UserSharedPrefManager sharedPrefManager;
    private Utils utils;
    private LikeApi likeApi;
    private boolean likeProcess = false;
    private SinglePlaylist singlePlaylist;
    private Playlist playlist;
    private MusicVerticalListAdapter musicVerticalListAdapter;
    private UserSharedPrefManager userSharedPrefManager;

    @BindView(R.id.img_single_playlist)
    ImageView img_playlist;
    @BindView(R.id.tv_single_playlist_name)
    TextView tv_playlist_name;
    @BindView(R.id.tv_single_playlist_count)
    TextView tv_playlist_count;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.tv_single_playlist_artist)
    TextView tv_playlist_artist;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.rc_single_playlist)
    RecyclerView rc_playlist_musics;
    @BindView(R.id.img_single_playlist_like)
    ImageView img_single_playlist_like;
    @BindView(R.id.fab_single_playlist_shuffle)
    FloatingActionButton fab_single_playlist_shuffle;


    public SinglePlaylistFragment(Playlist playlist) {
        this.playlist = playlist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_playlist, container, false);
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
        if (playlist.getImage() != null && !playlist.getImage().equals("")) {
            Glide.with(getActivity())
                    .load(playlist.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_playlist);
        } else {
            img_playlist.setImageResource(R.drawable.ic_music_placeholder);
        }
        tv_playlist_name.setText(playlist.getTitle());
        tv_header_title.setText(playlist.getTitle());
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        if (userSharedPrefManager.getShuffleMode()) {
            fab_single_playlist_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_single_playlist_shuffle.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
        } else {
            fab_single_playlist_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon));
            fab_single_playlist_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        }

    }

    private void getDataFromServer() {
        PlaylistApi playlistApi = new PlaylistApi(getActivity());
        playlistApi.single(playlist.getSlug(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    if (((SinglePlaylist) data).getMusics() != null) {
                        tv_playlist_count.setText(((SinglePlaylist) data).getMusics().size() + " " + getActivity().getString(R.string.music));
                    } else {
                        tv_playlist_count.setText("0" + " " + getActivity().getString(R.string.music));
                    }

                    if (sharedPrefManager.getLocal().equals("fa")) {
                        tv_playlist_artist.setTextDirection(View.TEXT_DIRECTION_RTL);
                        tv_playlist_name.setTextDirection(View.TEXT_DIRECTION_RTL);
                    }

                    setupMusic(((SinglePlaylist) data).getMusics());
                    singlePlaylist = ((SinglePlaylist) data);

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
        if (singlePlaylist == null) {
            return;
        }

        if (singlePlaylist.isHas_like()) {
            img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
        } else {
            img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
        }
        img_single_playlist_like.setVisibility(View.VISIBLE);


        img_single_playlist_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!singlePlaylist.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_single_playlist_like);
                img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                likeApi.like(playlist.getSlug(), "playlist", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                        } else {
                            singlePlaylist.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                likeApi.like(playlist.getSlug(), "playlist", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                        } else {
                            singlePlaylist.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_playlist_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });

    }


    private void setupMusic(List<Music> musics) {
        if (musics.size() > 0) {
            musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
            rc_playlist_musics.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true));
            rc_playlist_musics.setAdapter(musicVerticalListAdapter);
        }
    }

    @OnClick(R.id.fab_single_playlist_play)
    void fab_single_playlist_play() {
        EventBus.getDefault().post(new MusicEvent(getActivity(), singlePlaylist.getMusics(), singlePlaylist.getMusics().get(0).getSlug(), true, false));
    }

    @OnClick(R.id.fab_single_playlist_shuffle)
    void fab_single_playlist_shuffle() {
        if (userSharedPrefManager.getShuffleMode()) {
            userSharedPrefManager.setShuffleMode(false);
            fab_single_playlist_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon));
            fab_single_playlist_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        } else {
            userSharedPrefManager.setShuffleMode(true);
            fab_single_playlist_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_single_playlist_shuffle.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
        }
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
