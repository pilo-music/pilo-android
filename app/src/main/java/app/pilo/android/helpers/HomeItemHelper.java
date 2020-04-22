package app.pilo.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.adapters.ClickListenerPlayList;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.AlbumMusicGridListAdapter;
import app.pilo.android.adapters.OnStartDragListener;
import app.pilo.android.adapters.PlaylistsAdapter;
import app.pilo.android.adapters.VideoCarouselAdapter;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.MusicsFragment;
import app.pilo.android.fragments.SingleHomeFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Promotion;
import app.pilo.android.models.Video;

public class HomeItemHelper {
    private HomeFragment fragment;
    private List<WeakReference<RecyclerView.Adapter>> adapters;

    public HomeItemHelper() {
        adapters = new ArrayList<>();
    }

    public void init(HomeFragment fragment, List<Home> homes) {
        this.fragment = fragment;

        for (int i = 0; i < homes.size(); i++) {
            Home home = homes.get(i);
            if (home == null)
                continue;


            LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup parent = fragment.getView().findViewById(R.id.ll_main_layout);
            if (inflater == null)
                continue;

            switch (home.getType()) {
                case Home.TYPE_ARTISTS:
                    setupArtistCarousel(home, inflater, parent);
                    break;
                case Home.TYPE_MUSICS:
                    setupMusicsCarousel(home, inflater, parent);
                    break;
                case Home.TYPE_ALBUMS:
                    setupAlbumViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_PLAYLISTS:
                    setupPlaylistsViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_PROMOTION:
                    setupPromotion(home, inflater, parent);
                    break;
                case Home.TYPE_ALBUM_MUSIC_GRID:
                    setupAlbumMusicViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_MUSIC_GRID:
                case Home.TYPE_TRENDING:
                    setupMusicGridViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_PLAYLIST_GRID:
                    setupPlaylistsGridViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_VIDEOS:
                    setupVideoViewPager(home, inflater, parent);
                    break;
                case Home.TYPE_MUSIC_VERTICAL:
                    setupLastVerticalMusicList(home, inflater, parent);
                    break;
            }

        }


    }

    private void setupPromotion(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.promotion_item, parent);
        RoundedImageView riv_promotion = view.findViewById(R.id.riv_promotion);
        Glide.with(fragment.getActivity())
                .load(((Promotion) home.getData()).getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(riv_promotion);

        riv_promotion.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((Promotion) home.getData()).getUrl()));
            fragment.startActivity(browserIntent);
        });
    }

    private void setupPlaylistsViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.playlist_carousel, parent);
        RecyclerView rc_playlist_carousel = view.findViewById(R.id.rc_playlist_carousel);
        TextView tv_playlist_carousel_title = view.findViewById(R.id.tv_playlist_carousel_title);
        TextView tv_playlist_carousel_show_more = view.findViewById(R.id.tv_playlist_carousel_show_more);
        ShimmerFrameLayout sfl_playlist = view.findViewById(R.id.sfl_playlist);
        if (rc_playlist_carousel != null) {
            sfl_playlist.setVisibility(View.GONE);
            rc_playlist_carousel.setVisibility(View.VISIBLE);
            tv_playlist_carousel_title.setText(home.getName());
            PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(new WeakReference<>(fragment.getActivity()), ((List<Playlist>) home.getData()), R.layout.playlist_item_full_width);
            rc_playlist_carousel.setLayoutManager(new LinearLayoutManager(fragment.getActivity(), RecyclerView.HORIZONTAL, false));
            rc_playlist_carousel.setAdapter(playlistsAdapter);
            tv_playlist_carousel_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(playlistsAdapter));
        }
    }


    private void setupPlaylistsGridViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.playlist_grid_list, parent);
        RecyclerView rc_playlist_grid = view.findViewById(R.id.rc_playlist_grid);
        TextView tv_playlist_grid_title = view.findViewById(R.id.tv_playlist_grid_title);
        TextView tv_playlist_grid_show_more = view.findViewById(R.id.tv_playlist_grid_show_more);
        ShimmerFrameLayout sfl_playlist = view.findViewById(R.id.sfl_playlist);
        if (rc_playlist_grid != null) {
            sfl_playlist.setVisibility(View.GONE);
            tv_playlist_grid_title.setVisibility(View.VISIBLE);
            tv_playlist_grid_title.setText(home.getName());
            PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(new WeakReference<>(fragment.getActivity()), ((List<Playlist>) home.getData()), R.layout.playlist_item_full_width);
            rc_playlist_grid.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2));
            rc_playlist_grid.setAdapter(playlistsAdapter);
            tv_playlist_grid_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(playlistsAdapter));
        }
    }

    private void setupMusicGridViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.music_grid_list, parent);
        RecyclerView rc_music_grid = view.findViewById(R.id.rc_music_grid);
        TextView tv_music_grid_title = view.findViewById(R.id.tv_music_grid_title);
        TextView tv_music_grid_show_more = view.findViewById(R.id.tv_music_grid_show_more);
        if (rc_music_grid != null) {
            rc_music_grid.setVisibility(View.VISIBLE);
            tv_music_grid_title.setText(home.getName());
            MusicsListAdapter musicsListAdapter = new MusicsListAdapter(new WeakReference<>(fragment.getActivity()), ((List<Music>) home.getData()), R.layout.music_item_full_width);
            rc_music_grid.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2));
            rc_music_grid.setAdapter(musicsListAdapter);
            MusicsFragment musicsFragment = new MusicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", home.getName());
            musicsFragment.setArguments(bundle);
            tv_music_grid_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(musicsListAdapter));
        }
    }

    private void setupAlbumMusicViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.album_music_list, parent);
        RecyclerView rc_album_music = view.findViewById(R.id.rc_album_music);
        TextView tv_album_music_title = view.findViewById(R.id.tv_album_music_title);
        TextView tv_album_music_show_more = view.findViewById(R.id.tv_album_music_show_more);
        if (rc_album_music != null) {
            rc_album_music.setVisibility(View.VISIBLE);
            tv_album_music_title.setText(home.getName());
            AlbumMusicGridListAdapter trendListAdapter = new AlbumMusicGridListAdapter(new WeakReference<>(fragment.getActivity()), ((List<Object>) home.getData()));
            rc_album_music.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2));
            rc_album_music.setAdapter(trendListAdapter);
            MusicsFragment musicsFragment = new MusicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", home.getName());
            musicsFragment.setArguments(bundle);
            tv_album_music_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(trendListAdapter));
        }
    }


    private void setupMusicsCarousel(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.music_carousel, parent);
        RecyclerView rc_music_carousel = view.findViewById(R.id.rc_music_carousel);
        TextView tv_music_carousel_title = view.findViewById(R.id.tv_music_carousel_title);
        TextView tv_music_carousel_show_more = view.findViewById(R.id.tv_music_carousel_show_more);
        ShimmerFrameLayout sfl_music = view.findViewById(R.id.sfl_music);
        if (rc_music_carousel != null) {
            sfl_music.setVisibility(View.GONE);
            rc_music_carousel.setVisibility(View.VISIBLE);
            tv_music_carousel_title.setText(home.getName());
            MusicsListAdapter musicCarouselAdapter = new MusicsListAdapter(new WeakReference<>(fragment.getActivity()), ((List<Music>) home.getData()));

            rc_music_carousel.setLayoutManager(new LinearLayoutManager(fragment.getActivity(), RecyclerView.HORIZONTAL, false));
            rc_music_carousel.setAdapter(musicCarouselAdapter);
            MusicsFragment musicsFragment = new MusicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", home.getName());
            musicsFragment.setArguments(bundle);
            tv_music_carousel_show_more.setOnClickListener(v -> goToSingleHome(home));

            adapters.add(new WeakReference<>(musicCarouselAdapter));
        }
    }

    private void setupArtistCarousel(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.artist_carousel, parent);
        RecyclerView rc_artist_carousel = view.findViewById(R.id.rc_artist_carousel);
        TextView tv_artist_carousel_title = view.findViewById(R.id.tv_artist_carousel_title);
        TextView tv_artist_carousel_show_more = view.findViewById(R.id.tv_artist_carousel_show_more);
        ShimmerFrameLayout sfl_artist = view.findViewById(R.id.sfl_artist);
        if (rc_artist_carousel != null) {
            sfl_artist.setVisibility(View.GONE);
            rc_artist_carousel.setVisibility(View.VISIBLE);
            tv_artist_carousel_title.setText(home.getName());
            ArtistsListAdapter artistCarouselAdapter = new ArtistsListAdapter(new WeakReference<>(fragment.getActivity()), (List<Artist>) home.getData());
            rc_artist_carousel.setLayoutManager(new LinearLayoutManager(fragment.getActivity(), RecyclerView.HORIZONTAL, false));
            rc_artist_carousel.setAdapter(artistCarouselAdapter);

            adapters.add(new WeakReference<>(artistCarouselAdapter));
        }
        tv_artist_carousel_show_more.setOnClickListener(v -> goToSingleHome(home));
    }


    private void setupVideoViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.video_carousel, parent);
        SliderView sliderView = view.findViewById(R.id.imageSlider);
        TextView tv_video_carousel_title = view.findViewById(R.id.tv_video_carousel_title);
        TextView tv_video_carousel_show_more = view.findViewById(R.id.tv_video_carousel_show_more);
        ShimmerFrameLayout sfl_video = view.findViewById(R.id.sfl_video);
        if (sliderView != null) {
            sfl_video.setVisibility(View.GONE);
            sliderView.setVisibility(View.VISIBLE);
            tv_video_carousel_title.setText(home.getName());
            sliderView.setSliderAdapter(new VideoCarouselAdapter(new WeakReference<>(fragment.getActivity()), ((List<Video>) home.getData())));
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

            tv_video_carousel_show_more.setOnClickListener(v -> goToSingleHome(home));
        }
    }

    private void setupAlbumViewPager(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.album_carousel, parent);
        RecyclerView rc_album_carousel = view.findViewById(R.id.rc_album_carousel);
        TextView tv_album_carousel_title = view.findViewById(R.id.tv_album_carousel_title);
        TextView tv_album_carousel_show_more = view.findViewById(R.id.tv_album_carousel_show_more);
        ShimmerFrameLayout sfl_album = view.findViewById(R.id.sfl_album);
        if (rc_album_carousel != null) {
            sfl_album.setVisibility(View.GONE);
            rc_album_carousel.setVisibility(View.VISIBLE);
            tv_album_carousel_title.setText(home.getName());
            AlbumsListAdapter albumCarouselAdapter = new AlbumsListAdapter(new WeakReference<>(fragment.getActivity()), ((List<Album>) home.getData()));
            rc_album_carousel.setLayoutManager(new LinearLayoutManager(fragment.getActivity(), RecyclerView.HORIZONTAL, false));
            rc_album_carousel.setAdapter(albumCarouselAdapter);
            tv_album_carousel_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(albumCarouselAdapter));

        }
    }

    private void setupLastVerticalMusicList(Home home, LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.music_vertical_list, parent);
        RecyclerView rc_music_vertical = view.findViewById(R.id.rc_music_vertical);
        TextView tv_music_vertical_title = view.findViewById(R.id.tv_music_vertical_title);
        TextView tv_music_vertical_show_more = view.findViewById(R.id.tv_music_vertical_show_more);
        if (rc_music_vertical != null) {
            tv_music_vertical_title.setText(home.getName());
            //todo
            MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(fragment.getActivity()), ((List<Music>) home.getData()), new OnStartDragListener() {
                @Override
                public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

                }
            });
            rc_music_vertical.setLayoutManager(new LinearLayoutManager(fragment.getActivity(), RecyclerView.VERTICAL, false));
            rc_music_vertical.setAdapter(musicVerticalListAdapter);
            tv_music_vertical_show_more.setOnClickListener(v -> goToSingleHome(home));
            adapters.add(new WeakReference<>(musicVerticalListAdapter));
        }
    }


    private void goToSingleHome(Home home) {
        SingleHomeFragment singleHomeFragment = new SingleHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", home.getId());
        bundle.putString("type", home.getType());
        singleHomeFragment.setArguments(bundle);
        ((MainActivity) fragment.getActivity()).pushFragment(singleHomeFragment);

    }


    public void updateAdapters() {
        if (adapters != null) {
            for (int i = 0; i < adapters.size(); i++) {
                if (adapters.get(i).get() != null) {
                    adapters.get(i).get().notifyDataSetChanged();
                }
            }
        }
    }
}
