package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumMusicGridListAdapter;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.adapters.ClickListenerPlayList;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.PlaylistsAdapter;
import app.pilo.android.adapters.VideosAdapter;
import app.pilo.android.api.HomeApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Home;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleHomeFragment extends Fragment {
    private View view;
    private HomeApi homeApi;
    private int id;
    private String type;
    private int page = 1;
    private MusicsListAdapter musicsListAdapter;
    private AlbumsListAdapter albumsListAdapter;
    private VideosAdapter videosAdapter;
    private PlaylistsAdapter playlistsAdapter;
    private ArtistsListAdapter artistsListAdapter;
    private AlbumMusicGridListAdapter albumMusicGridListAdapter;

    private List<Music> musics;
    private List<Artist> artists;
    private List<Album> albums;
    private List<Playlist> playlistLists;
    private List<Video> videos;
    private List<Object> albumMusics;


    @BindView(R.id.rc_home)
    RecyclerView rc_home;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_home, container, false);
        ButterKnife.bind(this, view);
        homeApi = new HomeApi(getActivity());
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            type = getArguments().getString("type");
            swipeRefreshLayout.setOnRefreshListener(() -> {
                page = 1;
                getDataFromServer();
            });
            getDataFromServer();
        }

        musics = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        playlistLists = new ArrayList<>();
        videos = new ArrayList<>();
        albumMusics = new ArrayList<>();

        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        switch (type) {
            case Home.TYPE_ARTISTS:
                artistsListAdapter = new ArtistsListAdapter(new WeakReference<>(getActivity()), artists, R.layout.artist_item_full_width);
                rc_home.setAdapter(artistsListAdapter);
                break;
            case Home.TYPE_MUSICS:
            case Home.TYPE_MUSIC_GRID:
            case Home.TYPE_MUSIC_VERTICAL:
            case Home.TYPE_TRENDING:
                musicsListAdapter = new MusicsListAdapter(new WeakReference<>(getActivity()), musics, R.layout.music_item_full_width, new ClickListenerPlayList() {
                    @Override
                    public void onClick(int position) {

                    }

                    @Override
                    public void onItemZero() {

                    }
                });
                rc_home.setAdapter(musicsListAdapter);
                break;
            case Home.TYPE_ALBUMS:
                albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums, R.layout.album_item_full_width);
                rc_home.setAdapter(albumsListAdapter);
                break;
            case Home.TYPE_PLAYLISTS:
            case Home.TYPE_PLAYLIST_GRID:
                playlistsAdapter = new PlaylistsAdapter(new WeakReference<>(getActivity()), playlistLists, R.layout.playlist_item_full_width);
                rc_home.setAdapter(playlistsAdapter);
                break;
            case Home.TYPE_ALBUM_MUSIC_GRID:
                albumMusicGridListAdapter = new AlbumMusicGridListAdapter(new WeakReference<>(getActivity()), albumMusics, new ClickListenerPlayList() {
                    @Override
                    public void onClick(int position) {

                    }

                    @Override
                    public void onItemZero() {

                    }
                });
                break;
            case Home.TYPE_VIDEOS:
                videosAdapter = new VideosAdapter(new WeakReference<>(getActivity()), videos);
                rc_home.setAdapter(videosAdapter);
                break;
        }

        rc_home.setLayoutManager(layoutManager);
        rc_home.setHasFixedSize(true);
        rc_home.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        rc_home.addOnScrollListener(endlessScrollEventListener);
        return view;
    }

    private void getDataFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        homeApi.single(id, page, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (view != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (status) {
                        tv_header_title.setText(((Home) data).getName());
                        switch (type) {
                            case Home.TYPE_ARTISTS:
                                artists.addAll((List<Artist>) ((Home) data).getData());
                                page++;
                                artistsListAdapter.notifyDataSetChanged();
                                break;
                            case Home.TYPE_MUSICS:
                            case Home.TYPE_MUSIC_GRID:
                            case Home.TYPE_MUSIC_VERTICAL:
                            case Home.TYPE_TRENDING:
                                musics.addAll((List<Music>) ((Home) data).getData());
                                page++;
                                musicsListAdapter.notifyDataSetChanged();
                                break;
                            case Home.TYPE_ALBUMS:
                                albums.addAll((List<Album>) ((Home) data).getData());
                                page++;
                                albumsListAdapter.notifyDataSetChanged();
                                break;
                            case Home.TYPE_PLAYLISTS:
                            case Home.TYPE_PLAYLIST_GRID:
                                playlistLists.addAll((List<Playlist>) ((Home) data).getData());
                                page++;
                                playlistsAdapter.notifyDataSetChanged();
                                break;
                            case Home.TYPE_ALBUM_MUSIC_GRID:
                                albumMusics.addAll((List<Object>) ((Home) data).getData());
                                page++;
                                albumsListAdapter.notifyDataSetChanged();
                                break;
                            case Home.TYPE_VIDEOS:
                                videos.addAll((List<Video>) ((Home) data).getData());
                                page++;
                                videosAdapter.notifyDataSetChanged();
                                break;
                        }

                    } else {
                        new HttpErrorHandler(getActivity(), message);
                    }
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (view != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    new HttpErrorHandler(getActivity());
                }
            }
        });
    }
}
