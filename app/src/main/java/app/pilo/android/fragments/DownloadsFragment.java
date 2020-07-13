package app.pilo.android.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.pilo.android.R;
import app.pilo.android.adapters.DownloadsAdapter;
import app.pilo.android.adapters.EndlessScrollEventListener;
import app.pilo.android.adapters.SwipeToDeleteCallback;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Download;
import app.pilo.android.utils.MusicDownloader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadsFragment extends BaseFragment {

    @BindView(R.id.rc_downloads)
    RecyclerView recyclerView;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorlayout;

    private DownloadsAdapter downloadsAdapter;
    private List<Download> downloads;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloads = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        ButterKnife.bind(this, view);
        tv_header_title.setText(getString(R.string.downloads));


        downloadsAdapter = new DownloadsAdapter(new WeakReference<>(getActivity()), downloads);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(downloadsAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutAnimation(new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        enableSwipeToDeleteAndUndo();

        swipe_refresh_layout.setOnRefreshListener(() -> {
            downloads.clear();
            getDataFromDb();
        });
        getDataFromDb();

        return view;
    }

    private void getDataFromDb() {
        swipe_refresh_layout.setRefreshing(true);
        List<Download> data = AppDatabase.getInstance(getActivity()).downloadDao().get();
        downloads.addAll(data);
        downloadsAdapter.notifyDataSetChanged();
        swipe_refresh_layout.setRefreshing(false);
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Download item = downloadsAdapter.getData().get(position);

                downloadsAdapter.removeItem(position);


                Snackbar snackbar = Snackbar.make(coordinatorlayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    downloadsAdapter.restoreItem(item, position);
                    recyclerView.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }
}
