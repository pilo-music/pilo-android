package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.android.volley.error.VolleyError;

import app.pilo.android.R;
import app.pilo.android.activities.EditProfileActivity;
import app.pilo.android.activities.LoginActivity;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends BaseFragment {
    private User user;

    @BindView(R.id.cv_login)
    CardView cv_login;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        user = AppDatabase.getInstance(getActivity()).userDao().get();
        checkUserLogin();
        return view;
    }

    private void checkUserLogin() {
        if (user != null)
            cv_login.setVisibility(View.GONE);
        else {
            UserApi userApi = new UserApi(getActivity());
            userApi.me(new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {

                }

                @Override
                public void onGetError(@Nullable VolleyError error) {

                }
            });
        }
    }


    @OnClick(R.id.ll_profile_downloads)
    void downloads() {
        ((MainActivity) getActivity()).pushFragment(new DownloadsFragment());
    }

    @OnClick(R.id.img_profile_messages)
    void messages() {
        ((MainActivity) getActivity()).pushFragment(new MessagesFragment());
    }

    @OnClick(R.id.btn_profile_edit)
    void edit() {
        startActivity(new Intent(getActivity(), EditProfileActivity.class));
    }

    @OnClick(R.id.ll_profile_bookmarks)
    void bookmarks() {
        ((MainActivity) getActivity()).pushFragment(new BookmarksFragment());
    }

    @OnClick(R.id.ll_profile_contact_us)
    void contactUs() {
        ((MainActivity) getActivity()).pushFragment(new ContactUsFragment());
    }

    @OnClick(R.id.ll_profile_likes)
    void likes() {
        ((MainActivity) getActivity()).pushFragment(new LikesFragment());
    }

    @OnClick(R.id.btn_profile_login)
    void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @OnClick(R.id.ll_profile_settings)
    void settings(){
        ((MainActivity) getActivity()).pushFragment(new SettingsFragment());
    }

}
