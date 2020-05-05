package app.pilo.android.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import app.pilo.android.helpers.LocalHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalHelper.updateResources(this, "fa");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalHelper.updateResources(this, "fa");
    }
}
