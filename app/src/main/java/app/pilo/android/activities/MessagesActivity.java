package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.MessageListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MessageApi;
import app.pilo.android.models.Message;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MessagesActivity extends AppCompatActivity {

    @BindView(R.id.rc_messages)
    RecyclerView recyclerView;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private Unbinder unbinder;
    private int page = 1;
    private List<Message> messages;
    private MessageApi messageApi;
    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        unbinder = ButterKnife.bind(this);
        messages = new ArrayList<>();
        messageApi = new MessageApi(this);
        tv_header_title.setText(getString(R.string.messages));

        messageListAdapter = new MessageListAdapter(new WeakReference<>(MessagesActivity.this), messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(messageListAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(MessagesActivity.this, android.R.anim.fade_in)));
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
            messages.clear();
            getDataFromServer();
        });
        getDataFromServer();

    }

    private void getDataFromServer() {
        swipe_refresh_layout.setRefreshing(true);
        messageApi.get(null, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    messages.addAll((List<Message>) data);
                    page++;
                    messageListAdapter.notifyDataSetChanged();
                } else {
                    new HttpErrorHandler(MessagesActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
                new HttpErrorHandler(MessagesActivity.this);
            }
        });
    }


    @OnClick(R.id.fab_messages_add)
    void addMessage() {
        startActivity(new Intent(MessagesActivity.this, ContactUsActivity.class));
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
