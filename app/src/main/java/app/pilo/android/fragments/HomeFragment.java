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

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

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
import app.pilo.android.utils.CustomSnackBar;
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

    @BindView(R.id.rc_artist_carousel)
    RecyclerView rc_artist_carousel;
    @BindView(R.id.tv_artist_carousel_title)
    TextView tv_artist_carousel_title;
    @BindView(R.id.tv_artist_carousel_show_more)
    TextView tv_artist_carousel_show_more;

    @BindView(R.id.tv_video_carousel_title)
    TextView tv_video_carousel_title;
    @BindView(R.id.tv_video_carousel_show_more)
    TextView tv_video_carousel_show_more;
    @BindView(R.id.imageSlider)
    SliderView sliderView;

    @BindView(R.id.rc_album_carousel)
    RecyclerView rc_album_carousel;
    @BindView(R.id.tv_album_carousel_title)
    TextView tv_album_carousel_title;
    @BindView(R.id.tv_album_carousel_show_more)
    TextView tv_album_carousel_show_more;

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
        tv_music_carousel_title.setText(R.string.music_carousel_best_new);
        tv_artist_carousel_title.setText(R.string.artist_carousel_best_new);
        tv_video_carousel_title.setText(R.string.video_carousel_last);
        tv_album_carousel_title.setText(R.string.album_carousel_last);
        tv_music_vertical_title.setText(R.string.music_vertical_last);
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
                    CustomSnackBar.make(cl_view, getString(R.string.server_connection_error));
                }
            }

            @Override
            public void onGetError() {
                CustomSnackBar.make(cl_view, getString(R.string.server_connection_error));
            }
        });
    }

    private void setupBestMusicCarousel(List<Music> musics) {
        if (rc_music_carousel != null) {
            MusicCarouselAdapter musicCarouselAdapter = new MusicCarouselAdapter(getActivity(), musics);
            rc_music_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_music_carousel.setAdapter(musicCarouselAdapter);
        }
    }

    private void setupArtistCarousel(List<Artist> artists) {
        if (rc_artist_carousel != null) {
            ArtistCarouselAdapter artistCarouselAdapter = new ArtistCarouselAdapter(getActivity(), artists);
            rc_artist_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_artist_carousel.setAdapter(artistCarouselAdapter);
        }
    }

    private void setupVideoViewPager(List<Video> videos) {
        if (sliderView != null) {
            sliderView.setSliderAdapter(new VideoCarouselAdapter(getActivity(), videos));
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        }
    }

    private void setupAlbumViewPager(List<Album> albums) {
        if (rc_album_carousel != null) {
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