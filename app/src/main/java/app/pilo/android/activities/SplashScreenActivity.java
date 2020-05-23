package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import app.pilo.android.BuildConfig;
import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.VersionApi;
import app.pilo.android.helpers.LocalHelper;
import app.pilo.android.repositories.UserRepo;
import app.pilo.android.views.UpdateDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.error.VolleyError;

import java.util.Map;

public class SplashScreenActivity extends BaseActivity {
    @BindView(R.id.cpb_splash)
    CircularProgressBar cpb_splash;
    @BindView(R.id.ll_try_again)
    LinearLayout ll_try_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        checkUpdate();
        ll_try_again.setOnClickListener(v -> checkUpdate());
    }

    private void checkUpdate() {
        cpb_splash.setVisibility(View.VISIBLE);
        ll_try_again.setVisibility(View.GONE);
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
                    cpb_splash.setVisibility(View.GONE);
                    ll_try_again.setVisibility(View.VISIBLE);
                    new HttpErrorHandler(SplashScreenActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                cpb_splash.setVisibility(View.GONE);
                ll_try_again.setVisibility(View.VISIBLE);
                new HttpErrorHandler(SplashScreenActivity.this);
                Log.e("error", "onGetError: "+error.getMessage() );
            }
        });

    }

    private void showUpdateDialog(int version, int minVersion, String title, String update_description, String update_link) {
        new UpdateDialog(this, title, update_description, update_link, version, minVersion).showDialog();
    }

    public void checkForLogin() {
        String token = UserRepo.getInstance(this).get().getAccess_token();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (token != null && !token.equals(""))
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            else
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        }, 1000);

    }
}
