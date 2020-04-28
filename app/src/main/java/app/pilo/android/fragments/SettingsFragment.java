package app.pilo.android.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.pilo.android.R;
import app.pilo.android.activities.SplashScreenActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.views.CustomDialog;
import app.pilo.android.views.NotificationsSettingDialog;
import app.pilo.android.views.QualitySettingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    @BindView(R.id.tv_header_title)
    TextView tv_header_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        tv_header_title.setText(getString(R.string.profile_settings));
        return view;
    }


    @OnClick(R.id.ll_settings_logout)
    void ll_settings_logout() {
        new CustomDialog(getActivity(), getString(R.string.exit_dialog_title), getString(R.string.exit_dialog_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(Dialog dialog) {
                User user = UserRepo.getInstance(getActivity()).get();
                UserRepo.getInstance(getActivity()).delete(user);
                getActivity().startActivity(new Intent(getActivity(), SplashScreenActivity.class));
                getActivity().finishAffinity();
            }

            @Override
            public void onFailClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.tv_settings_telegram)
    void tv_settings_telegram() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/pilomusic"));
        getActivity().startActivity(browserIntent);
    }

    @OnClick(R.id.tv_settings_instagram)
    void tv_settings_instagram() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/pilo.app/"));
        getActivity().startActivity(browserIntent);
    }

    @OnClick(R.id.tv_settings_clear_downloads)
    void tv_settings_clear_downloads() {
        new CustomDialog(getActivity(), getString(R.string.exit_dialog_title), getString(R.string.exit_dialog_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(Dialog dialog) {
            }

            @Override
            public void onFailClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.tv_settings_clear_music_history)
    void tv_settings_clear_music_history() {
        new CustomDialog(getActivity(), getString(R.string.clear_music_history), getString(R.string.clear_music_history_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(Dialog dialog) {
                AppDatabase.getInstance(getContext()).playHistoryDao().deleteAll();
                dialog.dismiss();
            }

            @Override
            public void onFailClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.tv_settings_clear_search_history)
    void tv_settings_clear_search_history() {
        new CustomDialog(getActivity(), getString(R.string.clear_search_history), getString(R.string.clear_search_history_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(Dialog dialog) {
                AppDatabase.getInstance(getContext()).searchHistoryDao().deleteAll();
                dialog.dismiss();
            }

            @Override
            public void onFailClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.tv_settings_notifications)
    void tv_settings_notifications() {
        new NotificationsSettingDialog(getActivity()).show();
    }


    @OnClick(R.id.tv_settings_quality)
    void tv_settings_quality() {
        new QualitySettingDialog(getActivity()).show();
    }

    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }
}
