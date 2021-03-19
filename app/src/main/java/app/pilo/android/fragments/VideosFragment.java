package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.VideoVerticalListAdapter;
import app.pilo.android.adapters.VideosAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.VideoApi;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosFragment extends BaseFragment {
    @BindView(R.id.rc_videos)
    RecyclerView rc_videos;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;

    private VideoVerticalListAdapter videosAdapter;
    private VideoApi videoApi;
    private List<Video> videos;
    private int page = 1;
    private HashMap<String, Object> params;


    public VideosFragment() {
        params = new HashMap<>();
    }

    public VideosFragment(HashMap<String, Object> params) {
        this.params = params;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videos = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ButterKnife.bind(this, view);
        videoApi = new VideoApi(getActivity());

        if (getArguments() != null && getArguments().getString("title") != null) {
            tv_header_title.setText(getArguments().getString("title"));
        }else{
            tv_header_title.setText(getString(R.string.videos));
        }
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        videosAdapter = new VideoVerticalListAdapter(new WeakReference<>(getActivity()), videos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rc_videos.setAdapter(videosAdapter);
        rc_videos.setLayoutManager(layoutManager);
        rc_videos.setHasFixedSize(true);
        rc_videos.setNestedScrollingEnabled(false);

        EndlessScrollEventListener endlessScrollEventListener = new EndlessScrollEventListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNum, RecyclerView recyclerView) {
                getDataFromServer();
            }
        };

        rc_videos.addOnScrollListener(endlessScrollEventListener);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            getDataFromServer();
        });
        getDataFromServer();

        return view;
    }

    private void getDataFromServer() {
        swipeRefreshLayout.setRefreshing(true);
        params.put("page", page);
        params.put("count", 12);
        videoApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                if (status) {
                    videos.addAll((List<Video>) data);
                    page++;
                    videosAdapter.notifyDataSetChanged();
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (!checkView()) {
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);
                new HttpErrorHandler(getActivity());
            }
        });
    }
}
