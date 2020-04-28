package app.pilo.android.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
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
    private onClient onClient;
    private Context context;


    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.btn_success)
    Button btn_success;
    @BindView(R.id.btn_fail)
    Button btn_fail;


    public CustomDialog(Context context, String title, String body, String successButton, String failButton, onClient onClient) {
        this.title = title;
        this.body = body;
        this.successButton = successButton;
        this.failButton = failButton;
        this.onClient = onClient;
        this.context = context;
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
            btn_success.setVisibility(View.GONE);

        tv_title.setText(title);
        tv_description.setText(body);
        btn_success.setText(successButton);
        btn_fail.setText(failButton);

        btn_success.setOnClickListener(v -> onClient.onSuccessClick(dialog));
        btn_fail.setOnClickListener(v -> onClient.onFailClick(dialog));
        dialog.show();
    }

    public interface onClient {
        void onSuccessClick(Dialog dialog);

        void onFailClick(Dialog dialog);
    }
}
