package app.pilo.android.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import app.pilo.android.R;
import app.pilo.android.activities.SplashScreenActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateDialog extends BottomSheetDialogFragment {

    private Activity activity;
    private String title;
    private String description;
    private String link;
    private int version;
    private int minVersion;

    public final static String TAG = "UpdateDialog";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    public UpdateDialog(Activity activity, String title, String description, String link, int version, int minVersion) {
        this.activity = activity;
        this.title = title;
        this.description = description;
        this.link = link;
        this.version = version;
        this.minVersion = minVersion;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_dialog, container, false);
        this.setCancelable(version < minVersion);
        ButterKnife.bind(this, view);

        tvTitle.setText(title);
        tvDescription.setText(description);

        if (version < minVersion) {
            btnCancel.setVisibility(View.GONE);
        }

        btnUpdate.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            activity.startActivity(browserIntent);
        });

        btnCancel.setOnClickListener(v -> {
            this.dismiss();
            ((SplashScreenActivity) activity).checkForLogin();
        });
        return view;
    }
}
