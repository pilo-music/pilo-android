package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.models.Album;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rc_albums)
    RecyclerView rc_albums;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;

    private AlbumsListAdapter albumsListAdapter;
    private AlbumApi albumApi;
    private List<Album> albums;
    private LinearLayoutManager manager;

    private int page = 1;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, view);
        albumApi = new AlbumApi(getActivity());
        albums = new ArrayList<>();
        manager = new LinearLayoutManager(getActivity());
        tv_header_title.setText(getString(R.string.album_new));
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());


        albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums, R.layout.album_item_full_width);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rc_albums.setAdapter(albumsListAdapter);
        rc_albums.setLayoutManager(layoutManager);
        rc_albums.setHasFixedSize(true);
        rc_albums.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        rc_albums.addOnScrollListener(endlessScrollEventListener);



        swipe_refresh_layout.setOnRefreshListener(() -> {
            page = 1;
            albums.clear();
            getDataFromServer();
        });
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        swipe_refresh_layout.setRefreshing(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("count", 12);
        albumApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (view != null) {
                    swipe_refresh_layout.setRefreshing(false);
                    if (status) {
                        albums.addAll((List<Album>) data);
                        page++;
                        albumsListAdapter.notifyDataSetChanged();
                    } else {
                        new HttpErrorHandler(getActivity(), message);
                    }
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (view != null) {
                    swipe_refresh_layout.setRefreshing(false);
                    new HttpErrorHandler(getActivity());
                }
            }
        });
    }
}