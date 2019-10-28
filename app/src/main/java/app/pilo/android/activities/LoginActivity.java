package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.api.UserApi;
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
    @BindView(R.id.ll_login_google)
    LinearLayout ll_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_login)
    void login() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        boolean valid = true;

        if (!isValidEmail(email)) {
            et_email.setError(getString(R.string.email_not_valid));
            valid = false;
        }

        if (password.length() < 5) {
            et_password.setError(getString(R.string.password_small_length));
            valid = false;
        }


        if (valid) {
            UserApi userApi = new UserApi();
            userApi.login(email, password, new RequestHandler.RequestHandlerWithModel<User>() {
                @Override
                public void onGetInfo(String status, User data) {
                    if (status.equals("success")) {
                        doLogin(data);
                    }else if (status.equals("invalid"))
                    {
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
                }

                @Override
                public void onGetError() {
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
    }

    private void doLogin(User data) {
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
