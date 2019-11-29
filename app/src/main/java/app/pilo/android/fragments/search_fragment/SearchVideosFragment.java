package app.pilo.android.fragments.search_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.VideosAdapter;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchVideosFragment extends Fragment {

    private List<Video> videos;
    private View view;
    @BindView(R.id.rc_search_videos)
    RecyclerView rc_videos;


    public static SearchVideosFragment instance(List<Video> videos) {
        return new SearchVideosFragment(videos);
    }

    public SearchVideosFragment(List<Video> videos) {
        this.videos = videos;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_videos, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (rc_videos != null) {
            VideosAdapter videosAdapter = new VideosAdapter(new WeakReference<>(getActivity()), videos);
            rc_videos.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_videos.setAdapter(videosAdapter);
        }
    }
}
