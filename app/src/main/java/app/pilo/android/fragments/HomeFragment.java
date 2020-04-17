package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.error.VolleyError;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.HomeApi;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.helpers.HomeItemHelper;
import app.pilo.android.models.Home;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
    @BindView(R.id.cl_fragment_home)
    CoordinatorLayout cl_view;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        swipe_refresh_layout.setOnRefreshListener(() -> getHomeApi());
        getHomeApi();
        return view;
    }


    private void getHomeApi() {
        HomeApi homeApi = new HomeApi(getActivity());
        swipe_refresh_layout.setRefreshing(true);
        homeApi.get(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                swipe_refresh_layout.setRefreshing(false);
                if (status) {
                    new HomeItemHelper(HomeFragment.this, (List<Home>) data);
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                swipe_refresh_layout.setRefreshing(false);
                new HttpErrorHandler(getActivity());
            }
        });
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}