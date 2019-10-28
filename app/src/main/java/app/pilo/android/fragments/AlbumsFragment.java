package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.PaginationListener;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Album;
import app.pilo.android.utils.TypeFace;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    @BindView(R.id.rc_albums)
    RecyclerView rc_albums;
    @BindView(R.id.srl_albums)
    SwipeRefreshLayout srl_albums;

    private AlbumsListAdapter albumsListAdapter;
    private int page = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private AlbumApi albumApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, view);
        albumApi = new AlbumApi();
        initAdapter();
        return view;
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        albumsListAdapter = new AlbumsListAdapter(getActivity(), new ArrayList<>());
        rc_albums.setAdapter(albumsListAdapter);
        rc_albums.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                page++;
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void doApiCall() {
        srl_albums.setRefreshing(true);
        albumApi.get(null, page, new RequestHandler.RequestHandlerWithList<Album>() {
            @Override
            public void onGetInfo(String status, List<Album> list) {
                if (view != null) {
                    isLoading = false;
                    srl_albums.setRefreshing(false);

                    if (page != 1) albumsListAdapter.removeLoading();
                    if (list.size() != 0)
                        albumsListAdapter.addLoading();
                    else
                        isLastPage = true;


                    if (status.equals("success")) {
                        albumsListAdapter.addItems(list);
                    } else {
                        isLastPage = true;
                        Alerter.create(getActivity())
                                .setTitle(R.string.server_connection_error)
                                .setTextTypeface(TypeFace.font(getActivity()))
                                .setTitleTypeface(TypeFace.font(getActivity()))
                                .setButtonTypeface(TypeFace.font(getActivity()))
                                .setText(R.string.server_connection_message)
                                .setBackgroundColorRes(R.color.colorError)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                    }
                }
            }

            @Override
            public void onGetError() {
                if (view != null) {
                    srl_albums.setRefreshing(false);
                    Alerter.create(getActivity())
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(TypeFace.font(getActivity()))
                            .setTitleTypeface(TypeFace.font(getActivity()))
                            .setButtonTypeface(TypeFace.font(getActivity()))
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        page = 1;
        isLastPage = false;
        albumsListAdapter.clear();
        doApiCall();
    }
}