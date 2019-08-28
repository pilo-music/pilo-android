package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import app.pilo.android.R;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.LocalHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomBar;

    private boolean doubleBackToExitPressedOnce = false;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setupBottomNavigation();
        LocalHelper.updateResources(this,"fa");
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment curFrag = getSupportFragmentManager().getPrimaryNavigationFragment();
        if (curFrag != null) {
            transaction.detach(curFrag);
        }
        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment1 == null) {
            fragment1 = fragment;
            transaction.add(R.id.framelayout, fragment1, tag);
        } else {
            transaction.attach(fragment1);
        }
        transaction.setPrimaryNavigationFragment(fragment1);
        transaction.setReorderingAllowed(true);
        transaction.commitNowAllowingStateLoss();
    }

    private void setupBottomNavigation() {
        bottomBar.setSelectedItemId(0);
        loadFragment(new HomeFragment(), "MAIN_HOME_FRAGMENT");
        bottomBar.setOnNavigationItemSelectedListener(navigationItem -> {
            switch (navigationItem.getItemId()) {
                case R.id.homeFragment:
                    loadFragment(new HomeFragment(), "MAIN_HOME_FRAGMENT");
                    break;
                case R.id.browserFragment:
                    loadFragment(new BrowserFragment(), "MAIN_BROWSER_FRAGMENT");
                    break;
                case R.id.searchFragment:
                    loadFragment(new SearchFragment(), "MAIN_SEARCH_FRAGMENT");
                    break;
                case R.id.profileFragment:
                    loadFragment(new ProfileFragment(), "MAIN_PROFILE_FRAGMENT");
                    break;
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        try {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
