package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumCarouselAdapter;
import app.pilo.android.adapters.ArtistCarouselAdapter;
import app.pilo.android.adapters.MusicCarouselAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.VideoCarouselAdapter;
import app.pilo.android.api.HomeApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;
import app.pilo.android.utils.TypeFace;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {
    @BindView(R.id.cl_fragment_home)
    CoordinatorLayout cl_view;

    @BindView(R.id.rc_music_carousel)
    RecyclerView rc_music_carousel;
    @BindView(R.id.tv_music_carousel_title)
    TextView tv_music_carousel_title;
    @BindView(R.id.tv_music_carousel_show_more)
    TextView tv_music_carousel_show_more;
    @BindView(R.id.sfl_music)
    ShimmerFrameLayout sfl_music;

    @BindView(R.id.rc_artist_carousel)
    RecyclerView rc_artist_carousel;
    @BindView(R.id.tv_artist_carousel_title)
    TextView tv_artist_carousel_title;
    @BindView(R.id.tv_artist_carousel_show_more)
    TextView tv_artist_carousel_show_more;
    @BindView(R.id.sfl_artist)
    ShimmerFrameLayout sfl_artist;

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

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupViews();
        getHomeApi();
        return view;
    }

    private void setupViews() {
        tv_music_carousel_title.setText(R.string.new_best);
        tv_artist_carousel_title.setText(R.string.artist_best);
        tv_video_carousel_title.setText(R.string.video_new);
        tv_album_carousel_title.setText(R.string.album_new);
        tv_music_vertical_title.setText(R.string.music_new);
    }

    private void getHomeApi() {
        HomeApi homeApi = new HomeApi();
        homeApi.get(new RequestHandler.RequestHandlerWithModel<Home>() {
            @Override
            public void onGetInfo(String status, Home data) {
                if (status.equals("success")) {
                    setupBestMusicCarousel(data.getBest_musics());
                    setupArtistCarousel(data.getArtists());
                    setupVideoViewPager(data.getVideos());
                    setupAlbumViewPager(data.getAlbums());
                    setupLastVerticalMusicList(data.getLast_music());
                } else {
                    Alerter.create(getActivity())
                            .setTitle(R.string.server_connection_error)
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setTitleTypeface(TypeFace.font(getActivity()))
                            .setTextTypeface(TypeFace.font(getActivity()))
                            .setButtonTypeface(TypeFace.font(getActivity()))
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }
            }

            @Override
            public void onGetError() {
                Alerter.create(getActivity())
                        .setTitle(R.string.server_connection_error)
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorInt(R.color.design_default_color_error)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });
    }

    private void setupBestMusicCarousel(List<Music> musics) {
        if (rc_music_carousel != null) {
            sfl_music.setVisibility(View.GONE);
            rc_music_carousel.setVisibility(View.VISIBLE);
            MusicCarouselAdapter musicCarouselAdapter = new MusicCarouselAdapter(getActivity(), musics);
            rc_music_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_music_carousel.setAdapter(musicCarouselAdapter);
        }
    }

    private void setupArtistCarousel(List<Artist> artists) {
        if (rc_artist_carousel != null) {
            sfl_artist.setVisibility(View.GONE);
            rc_artist_carousel.setVisibility(View.VISIBLE);
            ArtistCarouselAdapter artistCarouselAdapter = new ArtistCarouselAdapter(getActivity(), artists);
            rc_artist_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_artist_carousel.setAdapter(artistCarouselAdapter);
        }

        tv_artist_carousel_show_more.setOnClickListener(v -> {

        });
    }

    private void setupVideoViewPager(List<Video> videos) {
        if (sliderView != null) {
            sfl_video.setVisibility(View.GONE);
            sliderView.setVisibility(View.VISIBLE);
            sliderView.setSliderAdapter(new VideoCarouselAdapter(getActivity(), videos));
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        }
    }

    private void setupAlbumViewPager(List<Album> albums) {
        if (rc_album_carousel != null) {
            sfl_album.setVisibility(View.GONE);
            rc_album_carousel.setVisibility(View.VISIBLE);
            AlbumCarouselAdapter albumCarouselAdapter = new AlbumCarouselAdapter(getActivity(), albums);
            rc_album_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_album_carousel.setAdapter(albumCarouselAdapter);
        }
    }

    private void setupLastVerticalMusicList(List<Music> musics) {
        if (rc_music_vertical != null) {
            MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(getActivity(), musics);
            rc_music_vertical.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_music_vertical.setAdapter(musicVerticalListAdapter);
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}