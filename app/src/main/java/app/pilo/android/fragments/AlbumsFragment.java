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

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.api.AlbumApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Album;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rc_albums)
    RecyclerView rc_albums;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
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
        getDataFromServer();
        albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums, R.layout.album_item_full_width);
        rc_albums.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rc_albums.setAdapter(albumsListAdapter);
        rc_albums.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        return view;
    }

    private void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        albumApi.get(null, page, new RequestHandler.RequestHandlerWithList<Album>() {
            @Override
            public void onGetInfo(String status, List<Album> list) {
                if (view != null) {
                    progressBar.setVisibility(View.GONE);
                    if (status.equals("success")) {
                        albums.addAll(list);
                        page++;
                        albumsListAdapter.notifyDataSetChanged();
                    } else {
                        Alerter.create(getActivity())
                                .setTitle(R.string.server_connection_error)
                                .setTextTypeface(Utils.font(getActivity()))
                                .setTitleTypeface(Utils.font(getActivity()))
                                .setButtonTypeface(Utils.font(getActivity()))
                                .setText(R.string.server_connection_message)
                                .setBackgroundColorRes(R.color.colorError)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                    }
                }
            }

            @Override
            public void onGetError(VolleyError error) {
                if (view != null) {
                    progressBar.setVisibility(View.GONE);
                    Alerter.create(getActivity())
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(Utils.font(getActivity()))
                            .setTitleTypeface(Utils.font(getActivity()))
                            .setButtonTypeface(Utils.font(getActivity()))
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }
            }
        });
    }
}