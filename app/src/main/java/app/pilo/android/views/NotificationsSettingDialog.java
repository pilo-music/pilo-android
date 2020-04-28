package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashMap;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;

public class NotificationsSettingDialog {
    private Context context;
    private UserApi userApi;
    private ProgressBar progress_bar;

    public NotificationsSettingDialog(Context context) {
        this.context = context;
    }

    public void show() {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.notification_setting_dialog);
        userApi = new UserApi(context);
        User user = UserRepo.getInstance(context).get();

        progress_bar = dialog.findViewById(R.id.progress_bar);
        SwitchMaterial switch_notification_setting_global = dialog.findViewById(R.id.switch_notification_setting_global);
        SwitchMaterial switch_notification_setting_music = dialog.findViewById(R.id.switch_notification_setting_music);
        SwitchMaterial switch_notification_setting_album = dialog.findViewById(R.id.switch_notification_setting_album);
        SwitchMaterial switch_notification_setting_video = dialog.findViewById(R.id.switch_notification_setting_video);


        if (user.isGlobal_notification())
            switch_notification_setting_global.setChecked(true);

        if (user.isMusic_notification())
            switch_notification_setting_music.setChecked(true);

        if (user.isAlbum_notification())
            switch_notification_setting_album.setChecked(true);

        if (user.isVideo_notification())
            switch_notification_setting_video.setChecked(true);


        LinearLayout ll_save = dialog.findViewById(R.id.ll_save);
        ll_save.setOnClickListener(v -> {
            progress_bar.setVisibility(View.GONE);
            ll_save.setEnabled(false);
            HashMap<String, Object> params = new HashMap<>();

            params.put("global_notification",switch_notification_setting_global.isChecked());
            params.put("music_notification",switch_notification_setting_music.isChecked());
            params.put("album_notification",switch_notification_setting_album.isChecked());
            params.put("video_notification",switch_notification_setting_video.isChecked());

            userApi.update(params, new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    if (status) {
                        user.setGlobal_notification(switch_notification_setting_global.isChecked());
                        user.setMusic_notification(switch_notification_setting_music.isChecked());
                        user.setAlbum_notification(switch_notification_setting_album.isChecked());
                        user.setVideo_notification(switch_notification_setting_video.isChecked());
                        UserRepo.getInstance(context).update(user);
                        dialog.dismiss();
                    } else {
                        ll_save.setEnabled(true);
                        progress_bar.setVisibility(View.GONE);
                        new HttpErrorHandler((MainActivity) context, message);
                    }
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    ll_save.setEnabled(true);
                    progress_bar.setVisibility(View.GONE);
                    new HttpErrorHandler((MainActivity) context);
                }
            });

        });


        dialog.show();
    }
}
