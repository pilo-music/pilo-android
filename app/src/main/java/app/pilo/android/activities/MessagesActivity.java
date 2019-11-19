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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.LikeListAdapter;
import app.pilo.android.adapters.MessageListAdapter;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.MessageApi;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.models.Like;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        unbinder = ButterKnife.bind(this);
        tv_header_title.setText(getString(R.string.messages));
        getDataFromServer();
    }

    private void getDataFromServer() {
        //todo : handle errors
        MessageApi messageApi = new MessageApi(this);
        swipe_refresh_layout.setRefreshing(true);
        messageApi.get(new RequestHandler.RequestHandlerWithList<Message>() {
            @Override
            public void onGetInfo(String status, List<Message> data) {
                swipe_refresh_layout.setRefreshing(false);
                if (status.equals("success")) {
                    MessageListAdapter messageListAdapter = new MessageListAdapter(MessagesActivity.this, data);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MessagesActivity.this, RecyclerView.VERTICAL, false));
                    recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(MessagesActivity.this, android.R.anim.fade_in)));
                    recyclerView.setAdapter(messageListAdapter);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }


    @OnClick(R.id.fab_messages_add)
    void addMessage() {
        startActivity(new Intent(MessagesActivity.this, ContactUsActivity.class));
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
