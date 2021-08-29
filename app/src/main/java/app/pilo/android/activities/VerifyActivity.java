package app.pilo.android.activities;

import androidx.annotation.Nullable;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.UserApi;
import app.pilo.android.databinding.ActivityVerifyBinding;
import app.pilo.android.models.User;
import app.pilo.android.repositories.UserRepo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.error.VolleyError;


public class VerifyActivity extends BaseActivity {

    ActivityVerifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String phone = getIntent().getExtras().getString("phone");
        if (phone == null || phone.isEmpty()) {
            startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
            finishAffinity();
        }
        binding.etPhone.setText(phone);
        binding.pbVerify.setOnClickListener(v -> verify());
    }


    private void verify() {
        String phone = binding.etPhone.getText().toString();
        String code = binding.etCode.getText().toString();
        if (!isValidPhoneNumber(phone)) {
            binding.etPhone.setError(getString(R.string.phone_not_valid));
            return;
        }

        if (code.length() < 4) {
            binding.etCode.setError(getString(R.string.invalid_verify_code));
            return;
        }

        binding.pbVerify.setProgress(true);
        new UserApi(VerifyActivity.this).login(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                binding.pbVerify.setProgress(false);
                if (status) {
                    doLogin((User) data);
                } else {
                    new HttpErrorHandler(VerifyActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                binding.pbVerify.setProgress(false);
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
//            Pushe.setUserEmail(data.getEmail());
            startActivity(new Intent(VerifyActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public static boolean isValidPhoneNumber(String target) {
        return target.length() == 11;
    }
}
