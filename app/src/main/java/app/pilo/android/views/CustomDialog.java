package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.pilo.android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialog {
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


    public void show() {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        if (!cancelable) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        dialog.setContentView(R.layout.custom_dialog);
        ButterKnife.bind(this, dialog);

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
            tv_success.setVisibility(View.VISIBLE);
            progress_bar.setVisibility(View.VISIBLE);
            onClient.onSuccessClick(dialog);
        });
        btn_fail.setOnClickListener(v -> onClient.onFailClick(dialog));
        dialog.show();
    }

    public interface onClient {
        void onSuccessClick(Dialog dialog);

        void onFailClick(Dialog dialog);
    }
}
