package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.tapadoo.alerter.Alerter;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.PiloButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.pushe.plus.Pushe;

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_confirm)
    EditText et_confirm;
    @BindView(R.id.pb_reset_password)
    PiloButton pb_reset_password;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        email = getIntent().getExtras().getString("email");
        if (email == null || email.isEmpty()) {
            startActivity(new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class));
            finishAffinity();
        }
        et_email.setText(email);
    }


    @OnClick(R.id.pb_reset_password)
    void pb_reset_password() {
        String email = et_email.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirm = et_confirm.getText().toString().trim();

        if (!validateData()) {
            return;
        }

        pb_reset_password.setProgress(true);
        new UserApi(ResetPasswordActivity.this).forgotPassword(email, code, password, confirm, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                pb_reset_password.setProgress(false);
                if (status) {
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                    finishAffinity();
                } else {
                    new HttpErrorHandler(ResetPasswordActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                pb_reset_password.setProgress(false);
                new HttpErrorHandler(ResetPasswordActivity.this);
            }
        });
    }


    private boolean validateData() {
        String password = et_password.getText().toString();
        String confirm = et_confirm.getText().toString();
        String code = et_code.getText().toString();

        if (code.length() < 4) {
            et_code.setError(getString(R.string.invalid_verify_code));
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

}