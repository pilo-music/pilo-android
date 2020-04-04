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
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.RequestHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.et_register_name)
    EditText et_name;
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
        userApi.register(et_name.getText().toString(), et_email.getText().toString(), et_password.getText().toString(), et_confirm.getText().toString(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progressBar.setVisibility(View.GONE);
                if (status) {
                    Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                    intent.putExtra("email", et_email.getText().toString());
                    startActivity(intent);
                } else {
                    new HttpErrorHandler(RegisterActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.GONE);
                new HttpErrorHandler(RegisterActivity.this);
            }
        });

    }

    private boolean validateData() {
        String password = et_password.getText().toString();
        String confirm = et_confirm.getText().toString();
        String name = et_name.getText().toString();

        if (name.length() < 1) {
            et_name.setError(getString(R.string.filed_required));
            return false;
        }

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
