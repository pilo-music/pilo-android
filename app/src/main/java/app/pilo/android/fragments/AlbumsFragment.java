package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import app.pilo.android.event.MusicEvent;
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
    private int page = 1;
    private HashMap<String, Object> params;

    public AlbumsFragment() {
        params = new HashMap<>();
    }

    public AlbumsFragment(HashMap<String, Object> params) {
        this.params = params;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albums = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, view);
        albumApi = new AlbumApi(getActivity());

        if (getArguments() != null && getArguments().getString("title") != null) {
            tv_header_title.setText(getArguments().getString("title"));
        } else {
            tv_header_title.setText(getString(R.string.albums));
        }
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());


        albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums, R.layout.item_full_width_album);
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
        params.put("page", page);
        params.put("count", 12);
        albumApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    albums.addAll((List<Album>) data);
                    page++;
                    albumsListAdapter.notifyDataSetChanged();
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (!checkView()) {
                    return;
                }
                swipe_refresh_layout.setRefreshing(false);
                new HttpErrorHandler(getActivity());

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        albumsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}