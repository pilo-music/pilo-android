package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import app.pilo.android.R;
import app.pilo.android.helpers.LocalHelper;
import app.pilo.android.repositories.UserRepo;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LocalHelper.updateResources(this, "fa");

        String token = UserRepo.getInstance(this).get().getAccess_token();
        if (token != null && !token.equals(""))
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        else
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }
}
