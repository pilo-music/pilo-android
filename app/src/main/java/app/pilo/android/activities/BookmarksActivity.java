package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import butterknife.Unbinder;

public class BookmarksActivity extends AppCompatActivity {
    @BindView(R.id.rc_bookmarks)
    RecyclerView recyclerView;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private Unbinder unbinder;

    private BookmarkListAdapter bookmarkListAdapter;
    private BookmarkApi bookmarkApi;
    private List<Bookmark> bookmarks;
    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        unbinder = ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.profile_likes));

        bookmarkApi = new BookmarkApi(this);
        bookmarks = new ArrayList<>();

        bookmarkListAdapter = new BookmarkListAdapter(new WeakReference<>(this), bookmarks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BookmarksActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(bookmarkListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(BookmarksActivity.this, android.R.anim.fade_in)));
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
    }

    private void getDataFromServer() {
        swipe_refresh_layout.setRefreshing(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("count", 12);
        bookmarkApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    bookmarks.addAll((List<Bookmark>) data);
                    page++;
                    bookmarkListAdapter.notifyDataSetChanged();
                } else {
                    new HttpErrorHandler(BookmarksActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
                new HttpErrorHandler(BookmarksActivity.this);
            }
        });
    }

    @OnClick(R.id.img_header_back)
    void back() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
