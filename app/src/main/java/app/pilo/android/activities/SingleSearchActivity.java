package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.adapters.ClickListenerPlayList;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.adapters.PlaylistsAdapter;
import app.pilo.android.adapters.VideosAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.models.Search;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleSearchActivity extends AppCompatActivity {

    private String query, type;
    private int page = 1;
    SearchApi searchApi;
    private ArtistsListAdapter artistsListAdapter;
    private MusicsListAdapter musicsListAdapter;
    private AlbumsListAdapter albumsListAdapter;
    private PlaylistsAdapter playlistsAdapter;
    private VideosAdapter videosAdapter;
    Search search;


    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.rc_items)
    RecyclerView rc_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_search);
        ButterKnife.bind(this);
        query = getIntent().getExtras().getString("query");
        type = getIntent().getExtras().getString("type");
        searchApi = new SearchApi(this);
        search = new Search();

        if (query == null || query.isEmpty() || type == null || type.isEmpty()) {
            startActivity(new Intent(SingleSearchActivity.this, SearchActivity.class));
            finish();
        }

        tv_header_title.setText(getString(R.string.results) + " : " + query);

        getDataFromServer();
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        switch (type) {
            case "artist":
                artistsListAdapter = new ArtistsListAdapter(new WeakReference<>(this), search.getArtists(), R.layout.artist_item_full_width);
                rc_items.setAdapter(artistsListAdapter);
                break;
            case "music":
                musicsListAdapter = new MusicsListAdapter(new WeakReference<>(this), search.getMusics(), R.layout.music_item_full_width);
                rc_items.setAdapter(musicsListAdapter);
                break;
            case "album":
                albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(this), search.getAlbums(), R.layout.album_item_full_width);
                rc_items.setAdapter(albumsListAdapter);
                break;
            case "playlist":
                playlistsAdapter = new PlaylistsAdapter(new WeakReference<>(this), search.getPlaylists(), R.layout.playlist_item_full_width);
                rc_items.setAdapter(playlistsAdapter);
                break;
            case "video":
                videosAdapter = new VideosAdapter(new WeakReference<>(this), search.getVideos());
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

    }

    private void getDataFromServer() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("type", type);
        params.put("page", page);
        progressBar.setVisibility(View.VISIBLE);
        searchApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    progressBar.setVisibility(View.GONE);
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
                    new HttpErrorHandler(SingleSearchActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.VISIBLE);
                new HttpErrorHandler(SingleSearchActivity.this);
            }
        });
    }

    @OnClick(R.id.img_header_back)
    void back() {
        finish();
    }
}
