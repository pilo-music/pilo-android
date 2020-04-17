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
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.api.ArtistApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.models.Artist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rc_artists)
    RecyclerView rc_artists;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;

    private ArtistsListAdapter artistsListAdapter;
    private ArtistApi artistApi;
    private List<Artist> artists;
    private LinearLayoutManager manager;

    private int page = 1;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.bind(this, view);
        artistApi = new ArtistApi(getActivity());
        artists = new ArrayList<>();
        manager = new LinearLayoutManager(getActivity());
        tv_header_title.setText(getString(R.string.artist_best));
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        artistsListAdapter = new ArtistsListAdapter(new WeakReference<>(getActivity()), artists, R.layout.artist_item_full_width);
        rc_artists.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rc_artists.setAdapter(artistsListAdapter);
        rc_artists.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getDataFromServer();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            getDataFromServer();
        });
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("count", 12);
        artistApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (view != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (status) {
                        artists.addAll((List<Artist>) data);
                        page++;
                        artistsListAdapter.notifyDataSetChanged();
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
