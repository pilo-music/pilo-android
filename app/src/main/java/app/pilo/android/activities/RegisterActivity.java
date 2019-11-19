package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.et_register_email)
    EditText et_email;
    @BindView(R.id.et_register_confirm)
    EditText et_confirm;
    @BindView(R.id.et_register_password)
    EditText et_password;
    @BindView(R.id.progress_bar_register)
    ProgressBar progressBar;

    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        userApi = new UserApi(this);
    }

    @OnClick(R.id.ll_register)
    void register() {
        if (validateData()) {
            sendDataToServer();
        }
    }

    @OnClick(R.id.tv_register_have_account)
    void goToLogin() {
        finish();
    }

    private void sendDataToServer() {
        progressBar.setVisibility(View.VISIBLE);
        userApi.register(et_email.getText().toString(), et_password.getText().toString(), new RequestHandler.RequestHandlerWithStatus() {
            @Override
            public void onGetInfo(String status) {
                progressBar.setVisibility(View.GONE);
                if (status.equals("exists")){
                    Alerter.create(RegisterActivity.this)
                            .setTitle(R.string.user_exist)
                            .setTextTypeface(TypeFace.font(RegisterActivity.this))
                            .setTitleTypeface(TypeFace.font(RegisterActivity.this))
                            .setButtonTypeface(TypeFace.font(RegisterActivity.this))
                            .setText(R.string.user_exist_description)
                            .setBackgroundColorRes(R.color.colorWarning)
                            .show();
                }else if (status.equals("success")){
                    Alerter.create(RegisterActivity.this)
                            .setTitle(R.string.register_success)
                            .setTextTypeface(TypeFace.font(RegisterActivity.this))
                            .setTitleTypeface(TypeFace.font(RegisterActivity.this))
                            .setButtonTypeface(TypeFace.font(RegisterActivity.this))
                            .setText(R.string.register_success_description)
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();
                }else{
                    Alerter.create(RegisterActivity.this)
                            .setTitle(R.string.server_connection_error)
                            .setTextTypeface(TypeFace.font(RegisterActivity.this))
                            .setTitleTypeface(TypeFace.font(RegisterActivity.this))
                            .setButtonTypeface(TypeFace.font(RegisterActivity.this))
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();
                }

            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Alerter.create(RegisterActivity.this)
                        .setTitle(R.string.server_connection_error)
                        .setTextTypeface(TypeFace.font(RegisterActivity.this))
                        .setTitleTypeface(TypeFace.font(RegisterActivity.this))
                        .setButtonTypeface(TypeFace.font(RegisterActivity.this))
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorRes(R.color.colorError)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });

    }

    private boolean validateData() {
        String password = et_password.getText().toString();
        String confirm = et_confirm.getText().toString();

        if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError(getString(R.string.email_not_valid));
            return false;
        }

        if (password.length() < 5) {
            et_password.setError(getString(R.string.password_small_length));
            return false;
        }

        if (!password.equals(confirm)) {
            et_confirm.setError(getString(R.string.password_not_match));
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
