package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumCarouselAdapter;
import app.pilo.android.adapters.ArtistCarouselAdapter;
import app.pilo.android.adapters.MusicCarouselAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.VideoViewPagerAdapter;
import app.pilo.android.models.Album;
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

    private List<Music> musics;
    private List<Artist> artists;
    private List<Video> videos;
    private List<Album> albums;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        generateFakeData();
        setupBestMusicCarousel();
        setupArtistCarousel();
        setupVideoViewPager();
        setupAlbumViewPager();
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

    private void setupAlbumViewPager() {
        tv_album_carousel_title.setText(R.string.album_carousel_last);
        AlbumCarouselAdapter albumCarouselAdapter = new AlbumCarouselAdapter(getActivity(), albums);
        rc_album_carousel.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rc_album_carousel.setAdapter(albumCarouselAdapter);
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
        albums = new ArrayList<>();
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
            Album album = new Album();
            album.setId(1487);
            album.setSlug("سالار-عقیلی-نگار");
            album.setTitle("نگار");
            album.setImage("https://dl.pilo.app/cover/Negar_5d42aea52529a.jpg");
            album.setUrl("https://dl.pilo.app/music/Negar_5d42aea551412.mp3");
            album.setIsbest(1);
            album.setHas_like(0);
            album.setHas_bookmark(0);
            album.setArtist_id(124);
            album.setArtist_name("سالار عقیلی");
            album.setArtist_slug("سالار-عقیلی");
            albums.add(album);
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
            video.setUrl("https://dl.pilo.app/video/Shahram%20Shabpareh%20-%20Lajbaz%20%5B720%5D.mp4");
            videos.add(video);
        }
    }
}