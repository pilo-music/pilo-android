package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ClickListenerPlayList;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.VideoCarouselAdapter;
import app.pilo.android.api.ArtistApi;
import app.pilo.android.api.FollowApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.SingleArtist;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SingleArtistFragment extends BaseFragment {
    private View view;
    private SingleArtist singleArtist;
    private Artist artist;
    private boolean followProcess = false;
    private FollowApi followApi;
    private MusicsListAdapter musicCarouselAdapter;
    private AlbumsListAdapter albumCarouselAdapter;
    private MusicVerticalListAdapter musicVerticalListAdapter;

    @BindView(R.id.tv_single_artist_name)
    TextView tv_artist_name;
    @BindView(R.id.tv_single_artist_album_count)
    TextView tv_album_count;
    @BindView(R.id.tv_single_artist_music_count)
    TextView tv_music_count;
    @BindView(R.id.tv_single_artist_video_count)
    TextView tv_video_count;
    @BindView(R.id.clv_single_artist)
    CircleImageView clv_artist;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;

    @BindView(R.id.tv_video_carousel_title)
    TextView tv_video_carousel_title;
    @BindView(R.id.tv_video_carousel_show_more)
    TextView tv_video_carousel_show_more;
    @BindView(R.id.imageSlider)
    SliderView sliderView;
    @BindView(R.id.sfl_video)
    ShimmerFrameLayout sfl_video;

    @BindView(R.id.rc_album_carousel)
    RecyclerView rc_album_carousel;
    @BindView(R.id.tv_album_carousel_title)
    TextView tv_album_carousel_title;
    @BindView(R.id.tv_album_carousel_show_more)
    TextView tv_album_carousel_show_more;
    @BindView(R.id.sfl_album)
    ShimmerFrameLayout sfl_album;

    @BindView(R.id.rc_music_vertical)
    RecyclerView rc_music_vertical;
    @BindView(R.id.tv_music_vertical_title)
    TextView tv_music_vertical_title;
    @BindView(R.id.tv_music_vertical_show_more)
    TextView tv_music_vertical_show_more;

    @BindView(R.id.rc_music_carousel)
    RecyclerView rc_music_carousel;
    @BindView(R.id.tv_music_carousel_title)
    TextView tv_music_carousel_title;
    @BindView(R.id.tv_music_carousel_show_more)
    TextView tv_music_carousel_show_more;
    @BindView(R.id.sfl_music)
    ShimmerFrameLayout sfl_music;
    @BindView(R.id.btn_single_artist_follow)
    Button btn_single_artist_follow;


    public SingleArtistFragment(Artist artist) {
        this.artist = artist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_artist, container, false);
        ButterKnife.bind(this, view);
        followApi = new FollowApi(getActivity());
        setupViews();
        getDataFromServer();
        return view;
    }

    private void setupViews() {
        if (artist.getImage() != null && !artist.getImage().equals("")) {
            Glide.with(getActivity())
                    .load(artist.getImage())
                    .placeholder(R.drawable.ic_artist_placeholder)
                    .error(R.drawable.ic_artist_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(clv_artist);
        } else {
            clv_artist.setImageResource(R.drawable.ic_artist_placeholder);
        }
        tv_music_vertical_title.setText(R.string.artist_best);
        tv_music_carousel_title.setText(R.string.music_new);
        tv_video_carousel_title.setText(R.string.video_new);
        tv_album_carousel_title.setText(R.string.album_new);
        tv_artist_name.setText(artist.getName());
        tv_header_title.setText(artist.getName());
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void getDataFromServer() {
        ArtistApi artistApi = new ArtistApi(getActivity());
        artistApi.single(artist.getSlug(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    tv_album_count.setText(String.valueOf(((SingleArtist) data).getArtist().getAlbum_count()));
                    tv_music_count.setText(String.valueOf(((SingleArtist) data).getArtist().getMusics_count()));
                    tv_video_count.setText(String.valueOf(((SingleArtist) data).getArtist().getVideo_count()));
                    setupBestMusicCarousel(((SingleArtist) data).getBest_musics());
                    setupVideoViewPager(((SingleArtist) data).getVideos());
                    setupAlbumViewPager(((SingleArtist) data).getAlbums());
                    setupLastVerticalMusicList(((SingleArtist) data).getLast_musics());
                    singleArtist = (SingleArtist) data;
                    setupFollow();
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

    private void setupFollow() {
        if (singleArtist == null)
            return;

        if (singleArtist.isIs_follow()) {
            btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_on));
            btn_single_artist_follow.setText(getString(R.string.follow_on));
        } else {
            btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_off));
            btn_single_artist_follow.setText(getString(R.string.follow_off));
        }

        btn_single_artist_follow.setVisibility(View.VISIBLE);

        btn_single_artist_follow.setOnClickListener(v -> {
            if (followProcess)
                return;
            if (!singleArtist.isIs_follow()) {
                followProcess = true;
                btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_on));
                btn_single_artist_follow.setText(getString(R.string.follow_on));
                followApi.follow(artist.getSlug(), "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_off));
                            btn_single_artist_follow.setText(getString(R.string.follow_off));
                        } else {
                            singleArtist.setIs_follow(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_off));
                        btn_single_artist_follow.setText(getString(R.string.follow_off));
                    }
                });
                followProcess = false;
            } else {
                followProcess = true;
                btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_off));
                btn_single_artist_follow.setText(getString(R.string.follow_off));
                followApi.follow(artist.getSlug(), "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_on));
                            btn_single_artist_follow.setText(getString(R.string.follow_on));
                        } else {
                            singleArtist.setIs_follow(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        btn_single_artist_follow.setBackground(getActivity().getDrawable(R.drawable.follow_background_on));
                        btn_single_artist_follow.setText(getString(R.string.follow_on));
                    }
                });
                followProcess = false;
            }
        });
    }


    private void setupBestMusicCarousel(List<Music> musics) {
        if (rc_music_carousel != null) {
            sfl_music.setVisibility(View.GONE);
            if (musics.size() > 0) {
                rc_music_carousel.setVisibility(View.VISIBLE);
                musicCarouselAdapter = new MusicsListAdapter(new WeakReference<>(getActivity()), musics);
                rc_music_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                rc_music_carousel.setAdapter(musicCarouselAdapter);
            } else {
                tv_music_carousel_show_more.setVisibility(View.GONE);
                tv_music_carousel_title.setVisibility(View.GONE);
            }
        }
    }

    private void setupVideoViewPager(List<Video> videos) {
        if (sliderView != null) {
            sfl_video.setVisibility(View.GONE);
            if (videos.size() > 0) {
                sliderView.setVisibility(View.VISIBLE);
                sliderView.setSliderAdapter(new VideoCarouselAdapter(new WeakReference<>(getActivity()), videos));
                sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

            } else {
                tv_video_carousel_show_more.setVisibility(View.GONE);
                tv_video_carousel_title.setVisibility(View.GONE);
            }
        }
    }

    private void setupAlbumViewPager(List<Album> albums) {
        if (rc_album_carousel != null) {
            sfl_album.setVisibility(View.GONE);
            if (albums.size() > 0) {
                rc_album_carousel.setVisibility(View.VISIBLE);
                albumCarouselAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums);
                rc_album_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                rc_album_carousel.setAdapter(albumCarouselAdapter);
            } else {
                tv_album_carousel_show_more.setVisibility(View.GONE);
                tv_album_carousel_title.setVisibility(View.GONE);
            }
        }
    }

    private void setupLastVerticalMusicList(List<Music> musics) {
        if (rc_music_vertical != null) {
            if (musics.size() > 0) {
                musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
                rc_music_vertical.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                rc_music_vertical.setAdapter(musicVerticalListAdapter);
            } else {
                tv_music_vertical_show_more.setVisibility(View.GONE);
                tv_music_vertical_title.setVisibility(View.GONE);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        if (musicCarouselAdapter != null)
            musicCarouselAdapter.notifyDataSetChanged();

        if (albumCarouselAdapter != null)
            albumCarouselAdapter.notifyDataSetChanged();

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
