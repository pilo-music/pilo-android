package app.pilo.android.activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.android.volley.error.VolleyError;

import java.util.Map;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnLogin.setOnClickListener(v -> login());
    }


    private void login() {
        String phone = binding.etPhone.getText().toString();
        if (!isValidPhoneNumber(phone)) {
            binding.etPhone.setError(getString(R.string.phone_not_valid));
            return;
        }

        binding.btnLogin.setProgress(true);
        UserApi userApi = new UserApi(this);
        userApi.login(phone, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                binding.btnLogin.setProgress(false);
                if (status) {
                    Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else {
                    new HttpErrorHandler(LoginActivity.this, message);
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    },1000);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                binding.btnLogin.setProgress(false);
                new HttpErrorHandler(LoginActivity.this);
            }
        });
    }


    public static boolean isValidPhoneNumber(String target) {
        return target.length() == 11;
    }
}
