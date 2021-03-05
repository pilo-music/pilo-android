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
import app.pilo.android.views.PiloButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.pb_register)
    PiloButton pb_register;

    private UserApi userApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        userApi = new UserApi(this);
    }

    @OnClick(R.id.pb_rest)
    void forgotPasswordCreate() {
        if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }
        pb_register.setProgress(true);
        userApi.forgotPasswordCreate(et_email.getText().toString(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                pb_register.setProgress(false);
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
                pb_register.setProgress(false);
                new HttpErrorHandler(ForgotPasswordActivity.this);
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
