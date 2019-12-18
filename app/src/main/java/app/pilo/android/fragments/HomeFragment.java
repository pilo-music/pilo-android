package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tapadoo.alerter.Alerter;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.VideoCarouselAdapter;
import app.pilo.android.api.HomeApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
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
        HomeApi homeApi = new HomeApi(getActivity());
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
                            .setTitleTypeface(Utils.font(getActivity()))
                            .setTextTypeface(Utils.font(getActivity()))
                            .setButtonTypeface(Utils.font(getActivity()))
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }
            }

            @Override
            public void onGetError(VolleyError error) {
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
            MusicsListAdapter musicCarouselAdapter = new MusicsListAdapter(new WeakReference<>(getActivity()), musics);
            rc_music_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_music_carousel.setAdapter(musicCarouselAdapter);
            MusicsFragment musicsFragment = new MusicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title",getString(R.string.music_best));
            musicsFragment.setArguments(bundle);
            tv_music_carousel_show_more.setOnClickListener(v -> ((MainActivity) getActivity()).pushFragment(new MusicsFragment()));
        }
    }

    private void setupArtistCarousel(List<Artist> artists) {
        if (rc_artist_carousel != null) {
            sfl_artist.setVisibility(View.GONE);
            rc_artist_carousel.setVisibility(View.VISIBLE);
            ArtistsListAdapter artistCarouselAdapter = new ArtistsListAdapter(new WeakReference<>(getActivity()), artists);
            rc_artist_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_artist_carousel.setAdapter(artistCarouselAdapter);
        }
        tv_artist_carousel_show_more.setOnClickListener(v -> ((MainActivity) getActivity()).pushFragment(new ArtistsFragment()));
    }

    private void setupVideoViewPager(List<Video> videos) {
        if (sliderView != null) {
            sfl_video.setVisibility(View.GONE);
            sliderView.setVisibility(View.VISIBLE);
            sliderView.setSliderAdapter(new VideoCarouselAdapter(new WeakReference<>(getActivity()), videos));
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        }
    }

    private void setupAlbumViewPager(List<Album> albums) {
        if (rc_album_carousel != null) {
            sfl_album.setVisibility(View.GONE);
            rc_album_carousel.setVisibility(View.VISIBLE);
            AlbumsListAdapter albumCarouselAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums);
            rc_album_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            rc_album_carousel.setAdapter(albumCarouselAdapter);
            tv_album_carousel_show_more.setOnClickListener(v -> ((MainActivity) getActivity()).pushFragment(new AlbumsFragment()));
        }
    }

    private void setupLastVerticalMusicList(List<Music> musics) {
        if (rc_music_vertical != null) {
            MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
            rc_music_vertical.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_music_vertical.setAdapter(musicVerticalListAdapter);
            MusicsFragment musicsFragment = new MusicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title",getString(R.string.music_new));
            musicsFragment.setArguments(bundle);
            tv_music_vertical_show_more.setOnClickListener(v -> ((MainActivity) getActivity()).pushFragment(new MusicsFragment()));
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}