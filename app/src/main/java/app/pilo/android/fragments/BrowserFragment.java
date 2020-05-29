package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.HomeApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.HomeItemHelper;
import app.pilo.android.models.Home;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BrowserFragment extends BaseFragment {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private HomeItemHelper homeItemHelper;
    private Unbinder unbinder;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browser, container, false);
        unbinder = ButterKnife.bind(this, view);
        swipe_refresh_layout.setOnRefreshListener(this::getHomeApi);
        homeItemHelper = new HomeItemHelper();
        getHomeApi();
        return view;
    }


    private void getHomeApi() {
        HomeApi homeApi = new HomeApi(getActivity());
        swipe_refresh_layout.setRefreshing(true);
        homeApi.getBrowse(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    homeItemHelper.init(BrowserFragment.this, (List<Home>) data);
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


        swipe_refresh_layout.setRefreshing(false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        homeItemHelper.updateAdapters();
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


    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
