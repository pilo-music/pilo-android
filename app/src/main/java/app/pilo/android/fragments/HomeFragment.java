package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.ArtistCarouselAdapter;
import app.pilo.android.adapters.MusicCarouselAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.VideoViewPagerAdapter;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
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

    @BindView(R.id.view_pager_videos)
    ViewPager view_pager_videos;
    @BindView(R.id.tv_video_carousel_title)
    TextView tv_video_carousel_title;
    @BindView(R.id.tv_video_carousel_show_more)
    TextView tv_video_carousel_show_more;

    @BindView(R.id.rc_music_vertical)
    RecyclerView rc_music_vertical;
    @BindView(R.id.tv_music_vertical_title)
    TextView tv_music_vertical_title;
    @BindView(R.id.tv_music_vertical_show_more)
    TextView tv_music_vertical_show_more;

    private List<Music> musics;
    private List<Artist> artists;
    private List<Video> videos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        generateFakeData();
        setupBestMusicCarousel();
        setupArtistCarousel();
        setupVideoViewPager();
        setupLastVerticalMusicList();
        return view;
    }

    private void setupBestMusicCarousel() {
        tv_music_carousel_title.setText(R.string.music_carousel_best_new);
        MusicCarouselAdapter musicCarouselAdapter = new MusicCarouselAdapter(getActivity(), musics);
        rc_music_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rc_music_carousel.setAdapter(musicCarouselAdapter);
    }

    private void setupArtistCarousel() {
        tv_artist_carousel_title.setText(R.string.artist_carousel_best_new);
        ArtistCarouselAdapter artistCarouselAdapter = new ArtistCarouselAdapter(getActivity(), artists);
        rc_artist_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rc_artist_carousel.setAdapter(artistCarouselAdapter);
    }

    private void setupVideoViewPager() {
        tv_video_carousel_title.setText(R.string.video_carousel_last);
        VideoViewPagerAdapter adapter = new VideoViewPagerAdapter(getActivity(), videos);
        view_pager_videos.setAdapter(adapter);
    }

    private void setupLastVerticalMusicList() {
        tv_music_vertical_title.setText(R.string.music_vertical_last);
        MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(getActivity(), musics);
        rc_music_vertical.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rc_music_vertical.setAdapter(musicVerticalListAdapter);
    }

    private void generateFakeData() {
        musics = new ArrayList<>();
        artists = new ArrayList<>();
        videos = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Music music = new Music();
            music.setId(1487);
            music.setSlug("سالار-عقیلی-نگار");
            music.setTitle("نگار");
            music.setImage("https://dl.pilo.app/cover/Negar_5d42aea52529a.jpg");
            music.setUrl("https://dl.pilo.app/music/Negar_5d42aea551412.mp3");
            music.setIsbest(1);
            music.setHas_like(0);
            music.setHas_bookmark(0);
            music.setArtist_id(124);
            music.setArtist_name("سالار عقیلی");
            music.setArtist_slug("سالار-عقیلی");
            musics.add(music);
        }

        for (int i = 0; i < 12; i++) {
            Artist artist = new Artist();
            artist.setId(437);
            artist.setName("سوگند");
            artist.setImage("https://dl.pilo.app/artist/sogand-68dd4098baee643-photo-thumb_5cbb11e9e9cf7.jpg");
            artist.setIsbest(1);
            artist.setSlug("سوگند");
            artists.add(artist);
        }
        for (int i = 0; i < 5; i++) {
            Video video = new Video();
            video.setId(53);
            video.setTitle("لجباز");
            video.setImage("https://dl.pilo.app/cover/Lajbaz_5d150b560f29a.jpg");
            video.setSlug("شهرام-شب-پره-لجباز");
            video.setArtist_name("سوگند");
            video.setIsbest(0);
            videos.add(video);
        }
    }
}