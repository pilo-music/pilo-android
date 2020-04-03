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
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.LikeListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Like;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LikesActivity extends AppCompatActivity {

    @BindView(R.id.rc_likes)
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
        setContentView(R.layout.activity_likes);
        unbinder = ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.profile_likes));
        getDataFromServer();
    }

    private void getDataFromServer() {
        LikeApi likeApi = new LikeApi(this);
        swipe_refresh_layout.setRefreshing(true);
        likeApi.get(null, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    LikeListAdapter likeListAdapter = new LikeListAdapter(new WeakReference<>(LikesActivity.this), (List<Like>) data);
                    recyclerView.setLayoutManager(new LinearLayoutManager(LikesActivity.this, RecyclerView.VERTICAL, false));
                    recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(LikesActivity.this, android.R.anim.fade_in)));
                    recyclerView.setAdapter(likeListAdapter);
                } else {
                    new HttpErrorHandler(LikesActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
                new HttpErrorHandler(LikesActivity.this);
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
