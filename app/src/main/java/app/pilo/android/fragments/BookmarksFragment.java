package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.pilo.android.R;
import app.pilo.android.adapters.BookmarkListAdapter;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.api.BookmarkApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.models.Bookmark;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarksFragment extends BaseFragment {
    @BindView(R.id.rc_bookmarks)
    RecyclerView recyclerView;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private BookmarkListAdapter bookmarkListAdapter;
    private BookmarkApi bookmarkApi;
    private List<Bookmark> bookmarks;
    private int page = 1;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, view);
        tv_header_title.setText(getString(R.string.profile_bookmarks));

        bookmarkApi = new BookmarkApi(getActivity());
        bookmarks = new ArrayList<>();

        bookmarkListAdapter = new BookmarkListAdapter(new WeakReference<>(getActivity()), bookmarks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true);
        recyclerView.setAdapter(bookmarkListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        recyclerView.addOnScrollListener(endlessScrollEventListener);

        swipe_refresh_layout.setOnRefreshListener(() -> {
            page = 1;
            bookmarks.clear();
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
        bookmarkApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    bookmarks.addAll((List<Bookmark>) data);
                    page++;
                    bookmarkListAdapter.notifyDataSetChanged();
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

    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }
}
