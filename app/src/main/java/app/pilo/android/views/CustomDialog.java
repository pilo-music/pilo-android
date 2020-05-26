package app.pilo.android.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import app.pilo.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialog extends BottomSheetDialogFragment {
    private String title;
    private String body;
    private String successButton;
    private String failButton;
    private boolean showSuccess = true;
    private boolean showFail = true;
    private boolean cancelable = true;
    private boolean showProgress = false;
    private onClient onClient;
    private Context context;

    public final static String TAG = "CustomDialog";


    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.tv_success)
    TextView tv_success;
    @BindView(R.id.ll_success)
    LinearLayout ll_success;
    @BindView(R.id.btn_fail)
    Button btn_fail;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;


    public CustomDialog(Context context, String title, String body, String successButton, String failButton, onClient onClient) {
        this.title = title;
        this.body = body;
        this.successButton = successButton;
        this.failButton = failButton;
        this.onClient = onClient;
        this.context = context;
    }

    public CustomDialog(Context context, String title, String body, String successButton, String failButton, boolean showProgress, onClient onClient) {
        this.title = title;
        this.body = body;
        this.successButton = successButton;
        this.failButton = failButton;
        this.onClient = onClient;
        this.context = context;
        this.showProgress = showProgress;
    }

    public CustomDialog(Context context, String title, String body, String successButton, String failButton, boolean showSuccess, boolean showFail, boolean cancelable, onClient onClient) {
        this.title = title;
        this.body = body;
        this.successButton = successButton;
        this.failButton = failButton;
        this.showSuccess = showSuccess;
        this.showFail = showFail;
        this.cancelable = cancelable;
        this.onClient = onClient;
        this.context = context;
    }

    public CustomDialog(Context context, String title, String body, String successButton, String failButton, boolean showSuccess, boolean showFail, boolean cancelable, boolean showProgress, onClient onClient) {
        this.title = title;
        this.body = body;
        this.successButton = successButton;
        this.failButton = failButton;
        this.showSuccess = showSuccess;
        this.showFail = showFail;
        this.cancelable = cancelable;
        this.onClient = onClient;
        this.context = context;
        this.showProgress = showProgress;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, container, false);
        this.setCancelable(cancelable);
        ButterKnife.bind(this, view);

        if (!showFail)
            btn_fail.setVisibility(View.GONE);
        if (!showSuccess)
            ll_success.setVisibility(View.GONE);


        tv_title.setText(title);
        tv_description.setText(body);
        tv_success.setText(successButton);
        btn_fail.setText(failButton);

        ll_success.setOnClickListener(v -> {
            ll_success.setEnabled(false);
            tv_success.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
            onClient.onSuccessClick(this);
        });
        btn_fail.setOnClickListener(v -> onClient.onFailClick(this));

        return view;
    }

    public interface onClient {
        void onSuccessClick(BottomSheetDialogFragment dialog);

        void onFailClick(BottomSheetDialogFragment dialog);
    }
}
