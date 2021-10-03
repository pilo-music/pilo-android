package app.pilo.android.activities;

import androidx.annotation.Nullable;

import app.pilo.android.BuildConfig;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.VersionApi;
import app.pilo.android.databinding.ActivitySplashScreenBinding;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.views.dialogs.UpdateDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.error.VolleyError;

import java.util.Map;

public class SplashScreenActivity extends BaseActivity {

    private ActivitySplashScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkUpdate();
        binding.btnRetry.setOnClickListener(v -> checkUpdate());
    }

    private void checkUpdate() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.btnRetry.setVisibility(View.GONE);
        new VersionApi(this).get(new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                int versionCode = BuildConfig.VERSION_CODE;
                if (status) {
                    String title = ((Map) data).get("update_title").toString();
                    String update_description = ((Map) data).get("update_description").toString();
                    String update_link = ((Map) data).get("update_link").toString();

                    int version = Integer.parseInt(((Map) data).get("version").toString());
                    int minVersion = Integer.parseInt(((Map) data).get("min_version").toString());

                    if (versionCode < version) {
                        showUpdateDialog(version, minVersion, title, update_description, update_link);
                    } else {
                        checkForLogin();
                    }

                } else {
                    new HttpErrorHandler(SplashScreenActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                binding.progress.setVisibility(View.GONE);
                binding.btnRetry.setVisibility(View.VISIBLE);
                new HttpErrorHandler(SplashScreenActivity.this);
                if (error != null) {
                    error.printStackTrace();
                }
            }
        });

    }

    private void showUpdateDialog(int version, int minVersion, String title, String update_description, String update_link) {
        new UpdateDialog(this, title, update_description, update_link, version, minVersion).show(getSupportFragmentManager(), UpdateDialog.TAG);
    }

    public void checkForLogin() {
        String token = UserRepo.getInstance(this).get().getAccess_token();

        if (token != null && !token.equals("")) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
        }
        finish();
    }
}
