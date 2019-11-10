package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.User;
import app.pilo.android.utils.TypeFace;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_login_email)
    EditText et_email;
    @BindView(R.id.et_login_password)
    EditText et_password;
    @BindView(R.id.progress_bar_login)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.ll_login)
    void login() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (!isValidEmail(email)) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }

        if (password.length() < 5) {
            et_password.setError(getString(R.string.password_small_length));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        UserApi userApi = new UserApi(this);
        userApi.login(email, password, new RequestHandler.RequestHandlerWithModel<User>() {
            @Override
            public void onGetInfo(String status, User data) {
                progressBar.setVisibility(View.INVISIBLE);
                switch (status) {
                    case "success":
                        doLogin(data);
                        break;
                    case "Unauthorized":
                        Alerter.create(LoginActivity.this)
                                .setTitle(R.string.server_connection_error)
                                .setTextTypeface(TypeFace.font(LoginActivity.this))
                                .setTitleTypeface(TypeFace.font(LoginActivity.this))
                                .setButtonTypeface(TypeFace.font(LoginActivity.this))
                                .setText(R.string.wrong_email_password)
                                .setBackgroundColorRes(R.color.colorWarning)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                        break;
                    case "not_verify":
                        Alerter.create(LoginActivity.this)
                                .setTitle(R.string.user_not_verify)
                                .setTextTypeface(TypeFace.font(LoginActivity.this))
                                .setTitleTypeface(TypeFace.font(LoginActivity.this))
                                .setButtonTypeface(TypeFace.font(LoginActivity.this))
                                .setText(R.string.server_connection_message)
                                .setBackgroundColorRes(R.color.colorWarning)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                        break;
                    case "deactive":
                        Alerter.create(LoginActivity.this)
                                .setTitle(R.string.user_deactivate)
                                .setTextTypeface(TypeFace.font(LoginActivity.this))
                                .setTitleTypeface(TypeFace.font(LoginActivity.this))
                                .setButtonTypeface(TypeFace.font(LoginActivity.this))
                                .setText(R.string.server_connection_message)
                                .setBackgroundColorRes(R.color.colorError)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                        break;
                    default:
                        Alerter.create(LoginActivity.this)
                                .setTitle(R.string.server_connection_error)
                                .setTextTypeface(TypeFace.font(LoginActivity.this))
                                .setTitleTypeface(TypeFace.font(LoginActivity.this))
                                .setButtonTypeface(TypeFace.font(LoginActivity.this))
                                .setText(R.string.server_connection_message)
                                .setBackgroundColorRes(R.color.colorError)
                                .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                                .show();
                        break;
                }
            }

            @Override
            public void onGetError(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Alerter.create(LoginActivity.this)
                        .setTitle(R.string.server_connection_error)
                        .setTextTypeface(TypeFace.font(LoginActivity.this))
                        .setTitleTypeface(TypeFace.font(LoginActivity.this))
                        .setButtonTypeface(TypeFace.font(LoginActivity.this))
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorRes(R.color.colorError)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });

    }

    @OnClick(R.id.ll_login_google)
    void loginGoogle() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.pilo.app/login/google/android"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.ll_register)
    void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.tv_login_forgot_password)
    void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }


    private void doLogin(User data) {
        if (data.getAccess_token() != null) {
            UserSharedPrefManager userSharedPrefManager = new UserSharedPrefManager(this);
            userSharedPrefManager.seToken(data.getAccess_token());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
