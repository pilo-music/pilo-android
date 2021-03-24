package app.pilo.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.SplashScreenActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Download;
import app.pilo.android.views.CustomDialog;
import app.pilo.android.views.NotificationsSettingDialog;
import app.pilo.android.views.QualitySettingDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment {

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
            public void onSuccessClick(BottomSheetDialogFragment dialog) {
                AppDatabase.NukeAllTables(getActivity());
                getActivity().startActivity(new Intent(getActivity(), SplashScreenActivity.class));
                getActivity().finishAffinity();
            }

            @Override
            public void onFailClick(BottomSheetDialogFragment dialog) {
                dialog.dismiss();
            }
        }).show(getActivity().getSupportFragmentManager(), CustomDialog.TAG);
    }

//    @OnClick(R.id.tv_settings_telegram)
//    void tv_settings_telegram() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/pilomusic"));
//        getActivity().startActivity(browserIntent);
//    }
//
//    @OnClick(R.id.tv_settings_instagram)
//    void tv_settings_instagram() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/pilo.app/"));
//        getActivity().startActivity(browserIntent);
//    }

    @OnClick(R.id.tv_settings_clear_downloads)
    void tv_settings_clear_downloads() {
        new CustomDialog(getActivity(), getString(R.string.exit_dialog_title), getString(R.string.exit_dialog_body), getString(R.string.yes), getString(R.string.no), true, new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(BottomSheetDialogFragment dialog) {
                List<Download> downloads = AppDatabase.getInstance(getActivity()).downloadDao().get();
                for (int i = 0; i < downloads.size(); i++) {
                    File file320 = new File(downloads.get(i).getPath320());
                    if (file320.exists())
                        file320.delete();

                    File file128 = new File(downloads.get(i).getPath128());
                    if (file128.exists())
                        file128.delete();

                    AppDatabase.getInstance(getContext()).downloadDao().delete(downloads.get(i));
                }

                dialog.dismiss();
            }

            @Override
            public void onFailClick(BottomSheetDialogFragment dialog) {
                dialog.dismiss();
            }
        }).show(getActivity().getSupportFragmentManager(), CustomDialog.TAG);
    }

    @OnClick(R.id.tv_settings_clear_music_history)
    void tv_settings_clear_music_history() {
        new CustomDialog(getActivity(), getString(R.string.clear_music_history), getString(R.string.clear_music_history_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(BottomSheetDialogFragment dialog) {
                AppDatabase.getInstance(getContext()).playHistoryDao().deleteAll();
                dialog.dismiss();
            }

            @Override
            public void onFailClick(BottomSheetDialogFragment dialog) {
                dialog.dismiss();
            }
        }).show(getActivity().getSupportFragmentManager(), CustomDialog.TAG);
    }

    @OnClick(R.id.tv_settings_clear_search_history)
    void tv_settings_clear_search_history() {
        new CustomDialog(getActivity(), getString(R.string.clear_search_history), getString(R.string.clear_search_history_body), getString(R.string.yes), getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(BottomSheetDialogFragment dialog) {
                AppDatabase.getInstance(getContext()).searchHistoryDao().nukeTable();
                dialog.dismiss();
            }

            @Override
            public void onFailClick(BottomSheetDialogFragment dialog) {
                dialog.dismiss();
            }
        }).show(getActivity().getSupportFragmentManager(), CustomDialog.TAG);
    }

    @OnClick(R.id.tv_settings_notifications)
    void tv_settings_notifications() {

        new NotificationsSettingDialog(getActivity()).show(getActivity().getSupportFragmentManager(), NotificationsSettingDialog.TAG);
    }


    @OnClick(R.id.tv_settings_quality)
    void tv_settings_quality() {
        new QualitySettingDialog(getActivity()).show(getActivity().getSupportFragmentManager(), QualitySettingDialog.TAG);
    }

    @OnClick(R.id.img_header_back)
    void back() {
        getActivity().onBackPressed();
    }
}
