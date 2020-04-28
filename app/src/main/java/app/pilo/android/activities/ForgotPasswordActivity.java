package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.et_forgot_password_email)
    EditText et_email;
    @BindView(R.id.progress_bar_forgot_password_create)
    ProgressBar progressBarCreate;
    @BindView(R.id.progress_bar_forgot_password)
    ProgressBar progressBar;
    @BindView(R.id.ll_forgot_password)
    LinearLayout ll_forgot_password;
    @BindView(R.id.ll_forgot_password_create)
    LinearLayout ll_forgot_password_create;
    @BindView(R.id.et_forgot_password_code)
    EditText et_forgot_password_code;
    @BindView(R.id.et_forgot_password_password)
    EditText et_forgot_password_password;
    @BindView(R.id.et_forgot_password_confirm)
    EditText et_forgot_password_confirm;
    @BindView(R.id.cv_code)
    CardView cv_code;
    @BindView(R.id.cv_confirm)
    CardView cv_confirm;
    @BindView(R.id.cv_password)
    CardView cv_password;

    private UserApi userApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        userApi = new UserApi(this);
    }

    @OnClick(R.id.ll_forgot_password_create)
    void forgotPasswordCreate() {
        if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }
        progressBarCreate.setVisibility(View.VISIBLE);
        userApi.forgotPasswordCreate(et_email.getText().toString(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progressBarCreate.setVisibility(View.GONE);
                if (status) {
                    Alerter.create(ForgotPasswordActivity.this)
                            .setTitle(message)
                            .setTextTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setTitleTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setButtonTypeface(Utils.font(ForgotPasswordActivity.this))
                            .setText("")
                            .setBackgroundColorRes(R.color.colorGreen)
                            .show();

                    ll_forgot_password_create.setVisibility(View.GONE);
                    ll_forgot_password.setVisibility(View.VISIBLE);
                    cv_code.setVisibility(View.VISIBLE);
                    cv_password.setVisibility(View.VISIBLE);
                    cv_confirm.setVisibility(View.VISIBLE);

                } else {
                    new HttpErrorHandler(ForgotPasswordActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBarCreate.setVisibility(View.GONE);
                new HttpErrorHandler(ForgotPasswordActivity.this);
            }
        });
    }


    @OnClick(R.id.ll_forgot_password)
    void forgotPassword() {
        if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }

        if (et_forgot_password_code.getText().length() == 0) {
            et_forgot_password_code.setError(getString(R.string.filed_required));
            return;
        }

        if (et_forgot_password_password.getText().length() == 0) {
            et_forgot_password_password.setError(getString(R.string.filed_required));
            return;
        }

        if (et_forgot_password_confirm.getText().length() == 0) {
            et_forgot_password_confirm.setError(getString(R.string.filed_required));
            return;
        }

        if (et_forgot_password_password.getText() != et_forgot_password_confirm.getText()) {
            et_forgot_password_confirm.setError(getString(R.string.password_not_match));
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        userApi.forgotPassword(et_email.getText().toString(), et_forgot_password_code.getText().toString(), et_forgot_password_password.getText().toString(), et_forgot_password_confirm.getText().toString(), new HttpHandler.RequestHandler() {
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

                    if (((User) data).getAccess_token() != null) {
                        User user = new User();
                        user.setAccess_token(((User) data).getAccess_token());
                        user.setEmail(((User) data).getEmail());
                        user.setPhone(((User) data).getPhone());
                        user.setBirth(((User) data).getBirth());
                        user.setGender(((User) data).getGender());
                        user.setPic(((User) data).getPic());
                        user.setGlobal_notification(((User) data).isGlobal_notification());
                        user.setMusic_notification(((User) data).isMusic_notification());
                        user.setAlbum_notification(((User) data).isAlbum_notification());
                        user.setVideo_notification(((User) data).isVideo_notification());

                        UserRepo.getInstance(ForgotPasswordActivity.this).insert(user);
                        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                        finishAffinity();
                    }
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
