package app.pilo.android.activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
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
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;
import co.pushe.plus.Pushe;

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
        String phone = binding.etPhone.getText().toString().trim();
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
                    if (((Map) data).get("status").toString().equals("login")) {
                        doLogin((User) ((Map) data).get("user"));
                    } else {
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    }
                } else {
                    new HttpErrorHandler(LoginActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                binding.btnLogin.setProgress(false);
                new HttpErrorHandler(LoginActivity.this);
            }
        });
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
            Pushe.setUserEmail(data.getEmail());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidPhoneNumber(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }
}
