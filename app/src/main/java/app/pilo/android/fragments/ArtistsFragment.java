package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.ArtistsListAdapter;
import app.pilo.android.api.ArtistApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Artist;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rc_artists)
    RecyclerView rc_artists;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;

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
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        artistApi.get(page, new RequestHandler.RequestHandlerWithList<Artist>() {
            @Override
            public void onGetInfo(String status, List<Artist> list) {
                if (view != null) {
                    if (status.equals("success")) {
                        artists.addAll(list);
                        page++;
                        artistsListAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
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
            }

            @Override
            public void onGetError(VolleyError error) {
                if (view != null) {
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
