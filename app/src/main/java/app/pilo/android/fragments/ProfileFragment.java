package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.pilo.android.R;
import app.pilo.android.activities.BookmarksActivity;
import app.pilo.android.activities.ContactUsActivity;
import app.pilo.android.activities.EditProfileActivity;
import app.pilo.android.activities.LikesActivity;
import app.pilo.android.activities.LoginActivity;
import app.pilo.android.activities.MessagesActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.img_profile_messages)
    void messages() {
        startActivity(new Intent(getActivity(), MessagesActivity.class));
    }

    @OnClick(R.id.ll_profile_edit)
    void edit() {
        startActivity(new Intent(getActivity(), EditProfileActivity.class));
    }

    @OnClick(R.id.ll_profile_bookmarks)
    void bookmarks() {
        startActivity(new Intent(getActivity(), BookmarksActivity.class));
    }

    @OnClick(R.id.ll_profile_contact_us)
    void contactUs() {
        startActivity(new Intent(getActivity(), ContactUsActivity.class));
    }

    @OnClick(R.id.ll_profile_likes)
    void likes() {
        startActivity(new Intent(getActivity(), LikesActivity.class));
    }

    @OnClick(R.id.btn_profile_login)
    void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

}
