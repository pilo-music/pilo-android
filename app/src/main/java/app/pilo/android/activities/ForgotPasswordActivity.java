package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.et_forgot_password_email)
    EditText et_email;
    @BindView(R.id.progress_bar_forgot_password)
    ProgressBar progressBar;

    private UserApi userApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        userApi = new UserApi(this);
    }

    @OnClick(R.id.ll_forgot_password)
    void sendDataToServer() {
        if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        userApi.forgotPasswordCreate(et_email.getText().toString(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progressBar.setVisibility(View.GONE);
                if (status) {
                    Alerter.create(ForgotPasswordActivity.this)
                            .setTitle(message)
                            .setTextTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setTitleTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setButtonTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setText("")
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();
                } else {
                    new HttpErrorHandler(ForgotPasswordActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                new HttpErrorHandler(ForgotPasswordActivity.this);
            }
        });
    }


    @OnClick(R.id.tv_login)
    void goToLogin() {
        finish();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
