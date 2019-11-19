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

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.BookmarkListAdapter;
import app.pilo.android.api.BookmarkApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Bookmark;
import app.pilo.android.models.User;
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
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        unbinder = ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.profile_likes));
        getDataFromServer();
    }

    private void getDataFromServer() {
        //todo : handle errors
        BookmarkApi bookmarkApi = new BookmarkApi(this);
        swipe_refresh_layout.setRefreshing(true);
        bookmarkApi.get(new RequestHandler.RequestHandlerWithList<Bookmark>() {
            @Override
            public void onGetInfo(String status, List<Bookmark> data) {
                swipe_refresh_layout.setRefreshing(false);
                if (status.equals("success")) {
                    BookmarkListAdapter bookmarkListAdapter = new BookmarkListAdapter(BookmarksActivity.this, data);
                    recyclerView.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this, RecyclerView.VERTICAL, false));
                    recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(BookmarksActivity.this, android.R.anim.fade_in)));
                    recyclerView.setAdapter(bookmarkListAdapter);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.img_header_back)
    void back(){
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
