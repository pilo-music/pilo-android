package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.views.PiloButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.pushe.plus.Pushe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.error.VolleyError;

import java.util.Map;

public class VerifyActivity extends BaseActivity {

    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.pb_verify)
    PiloButton pb_verify;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.bind(this);
        email = getIntent().getExtras().getString("email");
        if (email == null || email.isEmpty()) {
            startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
            finishAffinity();
        }
        et_email.setText(email);
    }


    @OnClick(R.id.pb_verify)
    void pb_verify(){
        String email = et_email.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if (!isValidEmail(email)) {
            et_email.setError(getString(R.string.email_not_valid));
            return;
        }

        if (code.length() < 4) {
            et_code.setError(getString(R.string.invalid_verify_code));
            return;
        }

        pb_verify.setProgress(true);
        new UserApi(VerifyActivity.this).verify(email, code, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                pb_verify.setProgress(false);
                if (status) {
                    doLogin((User) data);
                } else {
                    new HttpErrorHandler(VerifyActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                pb_verify.setProgress(false);
                new HttpErrorHandler(VerifyActivity.this);
            }
        });
    }

    private void doLogin(User data) {
        if (data.getAccess_token() != null) {
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
            Pushe.setUserEmail(data.getEmail());
            startActivity(new Intent(VerifyActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
