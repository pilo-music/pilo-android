package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.PlaylistsAdapter;
import app.pilo.android.adapters.VideosAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.models.Search;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleSearchFragment extends BaseFragment {

    private String query, type;
    private int page = 1;
    private SearchApi searchApi;
    private ArtistsListAdapter artistsListAdapter;
    private MusicsListAdapter musicsListAdapter;
    private AlbumsListAdapter albumsListAdapter;
    private PlaylistsAdapter playlistsAdapter;
    private VideosAdapter videosAdapter;
    private Search search;
    private View view;

    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.rc_items)
    RecyclerView rc_items;


    public SingleSearchFragment(String query, String type) {
        this.query = query;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_search, container, false);
        ButterKnife.bind(this, view);
        searchApi = new SearchApi(getActivity());
        search = new Search();

        if (query == null || query.isEmpty() || type == null || type.isEmpty()) {
            getActivity().onBackPressed();
        }

        tv_header_title.setText(getString(R.string.results) + " : " + query);

        getDataFromServer();
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        switch (type) {
            case "artist":
                artistsListAdapter = new ArtistsListAdapter(new WeakReference<>(getActivity()), search.getArtists(), R.layout.item_full_width_artist);
                rc_items.setAdapter(artistsListAdapter);
                break;
            case "music":
                musicsListAdapter = new MusicsListAdapter(new WeakReference<>(getActivity()), search.getMusics(), R.layout.item_full_width_music);
                rc_items.setAdapter(musicsListAdapter);
                break;
            case "album":
                albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), search.getAlbums(), R.layout.item_full_width_album);
                rc_items.setAdapter(albumsListAdapter);
                break;
            case "playlist":
                playlistsAdapter = new PlaylistsAdapter(new WeakReference<>(getActivity()), search.getPlaylists(), R.layout.item_full_width_playlist);
                rc_items.setAdapter(playlistsAdapter);
                break;
            case "video":
                videosAdapter = new VideosAdapter(new WeakReference<>(getActivity()), search.getVideos());
                rc_items.setAdapter(videosAdapter);
                break;
        }

        rc_items.setLayoutManager(layoutManager);
        rc_items.setHasFixedSize(true);
        rc_items.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        rc_items.addOnScrollListener(endlessScrollEventListener);

        return view;
    }

    private void getDataFromServer() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("type", type);
        params.put("page", page);
        searchApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                if (status) {
                    Search result = (Search) data;
                    switch (type) {
                        case "artist":
                            search.getArtists().addAll(result.getArtists());
                            artistsListAdapter.notifyDataSetChanged();
                            break;
                        case "music":
                            search.getMusics().addAll(result.getMusics());
                            musicsListAdapter.notifyDataSetChanged();
                            break;
                        case "album":
                            search.getAlbums().addAll(result.getAlbums());
                            albumsListAdapter.notifyDataSetChanged();
                            break;
                        case "playlist":
                            search.getPlaylists().addAll(result.getPlaylists());
                            playlistsAdapter.notifyDataSetChanged();
                            break;
                        case "video":
                            search.getVideos().addAll(result.getVideos());
                            videosAdapter.notifyDataSetChanged();
                            break;
                    }
                    page++;
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

    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }
}
