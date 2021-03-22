package app.pilo.android.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import app.pilo.android.db.AppDatabase;

public class BaseActivity extends AppCompatActivity {

    public boolean active = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account == null){
//            logout();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    private void logout(){
        AppDatabase.NukeAllTables(this);
        startActivity(new Intent(this, SplashScreenActivity.class));
        finishAffinity();
    }
}
