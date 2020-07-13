package app.pilo.android.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.error.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.HashMap;
import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;

public class NotificationsSettingDialog  extends BottomSheetDialogFragment {
    private Context context;
    private UserApi userApi;
    private ProgressBar progress_bar;

    public final static String TAG = "NotificationsSettingDialog";


    public NotificationsSettingDialog(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_setting_dialog, container, false);

        userApi = new UserApi(context);
        User user = UserRepo.getInstance(context).get();

        progress_bar = view.findViewById(R.id.progress_bar);
        SwitchMaterial switch_notification_setting_global = view.findViewById(R.id.switch_notification_setting_global);
        SwitchMaterial switch_notification_setting_music = view.findViewById(R.id.switch_notification_setting_music);
        SwitchMaterial switch_notification_setting_album = view.findViewById(R.id.switch_notification_setting_album);
        SwitchMaterial switch_notification_setting_video = view.findViewById(R.id.switch_notification_setting_video);


        if (user.isGlobal_notification())
            switch_notification_setting_global.setChecked(true);

        if (user.isMusic_notification())
            switch_notification_setting_music.setChecked(true);

        if (user.isAlbum_notification())
            switch_notification_setting_album.setChecked(true);

        if (user.isVideo_notification())
            switch_notification_setting_video.setChecked(true);


        LinearLayout ll_save = view.findViewById(R.id.ll_save);
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
                        NotificationsSettingDialog.this.dismiss();
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

        return view;
    }
}