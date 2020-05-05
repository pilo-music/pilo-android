package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.error.VolleyError;

import java.util.Map;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_login_email)
    EditText et_email;
    @BindView(R.id.et_login_password)
    EditText et_password;
    @BindView(R.id.progress_bar_login)
    ProgressBar progressBar;
    @BindView(R.id.ll_login)
    LinearLayout ll_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ll_login.setOnClickListener(v -> login());
    }


    private void login() {
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
        ll_login.setEnabled(false);
        UserApi userApi = new UserApi(this);
        userApi.login(email, password, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progressBar.setVisibility(View.GONE);
                ll_login.setEnabled(true);
                if (status) {
                    if (((Map) data).get("status").toString().equals("login")) {
                        doLogin((User) ((Map) data).get("user"));
                    } else {
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                } else {
                    new HttpErrorHandler(LoginActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                ll_login.setEnabled(true);
                new HttpErrorHandler(LoginActivity.this);
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
        if (data != null && data.getAccess_token() != null) {
            User user = new User();
            user.setAccess_token(data.getAccess_token());
            user.setEmail(data.getEmail());
            user.setPhone(data.getPhone());
            user.setBirth(data.getBirth());
            user.setGender(data.getGender());
            user.setPic(data.getPic());
            user.setGlobal_notification(data.isGlobal_notification());
            user.setMusic_notification(data.isMusic_notification());
            user.setAlbum_notification(data.isAlbum_notification());
            user.setVideo_notification(data.isVideo_notification());
            UserRepo.getInstance(this).insert(user);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
