package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.pilo.android.R;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.MusicsListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MusicApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicsFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rc_musics)
    RecyclerView rc_musics;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;

    private MusicsListAdapter musicsListAdapter;
    private MusicApi musicApi;
    private List<Music> musics;
    private int page = 1;
    private HashMap<String, Object> params;

    public MusicsFragment() {
        params = new HashMap<>();
    }

    public MusicsFragment(HashMap<String, Object> params) {
        this.params = params;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musics = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_musics, container, false);
        ButterKnife.bind(this, view);
        musicApi = new MusicApi(getActivity());

        if (getArguments() != null && getArguments().getString("title") != null) {
            tv_header_title.setText(getArguments().getString("title"));
        }else{
            tv_header_title.setText(getString(R.string.musics));
        }
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        musicsListAdapter = new MusicsListAdapter(new WeakReference<>(getActivity()), musics, R.layout.item_full_width_music);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rc_musics.setAdapter(musicsListAdapter);
        rc_musics.setLayoutManager(layoutManager);
        rc_musics.setHasFixedSize(true);
        rc_musics.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        rc_musics.addOnScrollListener(endlessScrollEventListener);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            musics.clear();
            getDataFromServer();
        });
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        params.put("page", page);
        params.put("count", 12);
        musicApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                if (status) {
                    musics.addAll((List<Music>) data);
                    page++;
                    musicsListAdapter.notifyDataSetChanged();
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }

            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (checkView()) {
                    swipeRefreshLayout.setRefreshing(false);
                    new HttpErrorHandler(getActivity());
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        musicsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
