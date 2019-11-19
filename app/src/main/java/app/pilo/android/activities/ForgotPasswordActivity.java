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
import app.pilo.android.api.RequestHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.utils.TypeFace;
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
        userApi.forgotPassword(et_email.getText().toString(), new RequestHandler.RequestHandlerWithData() {
            @Override
            public void onGetInfo(String status, String data) {
                progressBar.setVisibility(View.GONE);
                if (status.equals("success")) {
                    Alerter.create(ForgotPasswordActivity.this)
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setTitleTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setButtonTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setText(R.string.forgot_password_send)
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();
                } else {
                    Alerter.create(ForgotPasswordActivity.this)
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setTitleTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setButtonTypeface(TypeFace.font(ForgotPasswordActivity.this))
                            .setText(data)
                            .setBackgroundColorRes(R.color.colorWarning)
                            .show();
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Alerter.create(ForgotPasswordActivity.this)
                        .setTitle(R.string.server_connection_error)
                        .setTextTypeface(TypeFace.font(ForgotPasswordActivity.this))
                        .setTitleTypeface(TypeFace.font(ForgotPasswordActivity.this))
                        .setButtonTypeface(TypeFace.font(ForgotPasswordActivity.this))
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorRes(R.color.colorError)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });
    }


    @OnClick(R.id.tv_login)
    void goToLogin(){
        finish();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
