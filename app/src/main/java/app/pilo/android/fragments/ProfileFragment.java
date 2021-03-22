package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import app.pilo.android.R;
import app.pilo.android.activities.EditProfileActivity;
import app.pilo.android.activities.LoginActivity;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends BaseFragment {
    @BindView(R.id.civ_profile_user)
    CircleImageView civ_profile_user;
    @BindView(R.id.tv_profile_email)
    TextView tv_profile_email;

    User user =  new User();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        user = AppDatabase.getInstance(getContext()).userDao().get();
        tv_profile_email.setText(user.getName());
        if (user.getPic() != ""){
            Glide.with(getActivity())
                    .load(user.getPic())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(civ_profile_user);
        }
        checkUserLogin();
        return view;
    }

    private void checkUserLogin() {
        UserApi userApi = new UserApi(getActivity());
        userApi.me(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                tv_profile_email.setText(((User) data).getEmail());
                Glide.with(getActivity())
                        .load(((User) data).getPic())
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(civ_profile_user);
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {

            }
        });
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

    @OnClick(R.id.ll_profile_contact_us)
    void contactUs() {
        ((MainActivity) getActivity()).pushFragment(new ContactUsFragment());
    }

    @OnClick(R.id.ll_profile_likes)
    void likes() {
        ((MainActivity) getActivity()).pushFragment(new LikesFragment());
    }

    @OnClick(R.id.ll_profile_settings)
    void settings() {
        ((MainActivity) getActivity()).pushFragment(new SettingsFragment());
    }

}
