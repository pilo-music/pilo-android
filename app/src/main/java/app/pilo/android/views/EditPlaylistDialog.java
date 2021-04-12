package app.pilo.android.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.PlaylistApi;
import app.pilo.android.models.Playlist;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPlaylistDialog {
    private Context context;
    private Playlist playlist;
    private PlaylistApi playlistApi;
    private Dialog dialog;
    private onClick onClick;

    @BindView(R.id.et_edit_playlist_name)
    EditText et_edit_playlist_name;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.tv_edit_playlist)
    TextView tv_edit_playlist;


    public EditPlaylistDialog(Context context, Playlist playlist, onClick onClick) {
        this.context = context;
        this.playlist = playlist;
        playlistApi = new PlaylistApi(context);
        this.onClick = onClick;
    }

    public void show() {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit_playlist);
        ButterKnife.bind(this, dialog);
        et_edit_playlist_name.setText(playlist.getTitle());

        dialog.show();
    }

    @OnClick(R.id.ll_edit_playlist_save)
    void ll_edit_playlist_save() {
        if (et_edit_playlist_name.getText().length() < 3)
            et_edit_playlist_name.setError("");
        tv_edit_playlist.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        playlistApi.edit(playlist.getSlug(), et_edit_playlist_name.getText().toString(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                tv_edit_playlist.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                if (status) {
                    playlist.setTitle(et_edit_playlist_name.getText().toString());
                    Alerter.create((Activity) context)
                            .setTitle(message)
                            .setTextTypeface(Utils.font(context))
                            .setTitleTypeface(Utils.font(context))
                            .setButtonTypeface(Utils.font(context))
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();
                    onClick.onEdit(dialog, playlist);
                } else
                    new HttpErrorHandler((MainActivity) context, message);
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                tv_edit_playlist.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                new HttpErrorHandler((MainActivity) context);
            }
        });
    }


    @OnClick(R.id.btn_edit_playlist_close)
    void btn_edit_playlist_close() {
        dialog.dismiss();
    }


    @OnClick(R.id.ll_edit_playlist_delete)
    void ll_edit_playlist_delete() {
        new CustomDialog(context, context.getString(R.string.play_list_delete), context.getString(R.string.play_list_delete_body), context.getString(R.string.yes), context.getString(R.string.no), new CustomDialog.onClient() {
            @Override
            public void onSuccessClick(BottomSheetDialogFragment dialog1) {
                playlistApi.delete(playlist.getSlug(), new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (status) {
                            onClick.onDelete(dialog1, dialog, playlist);
                        } else
                            new HttpErrorHandler((MainActivity) context, message);
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                    }
                });
            }

            @Override
            public void onFailClick(BottomSheetDialogFragment dialog1) {
                dialog1.dismiss();
            }
        }).show(((MainActivity)(context)).getSupportFragmentManager(), CustomDialog.TAG);
    }


    public interface onClick {
        void onEdit(Dialog dialog, Playlist playlist);

        void onEdit(BottomSheetDialogFragment dialog, Playlist playlist);

        void onDelete(Dialog dialog1, Dialog dialog, Playlist playlist);

        void onDelete(BottomSheetDialogFragment dialog1, Dialog dialog, Playlist playlist);
    }
}
