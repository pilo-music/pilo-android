package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.squareup.picasso.Picasso;

import app.pilo.android.R;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.LocalHelper;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    // main activity
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomBar;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout sliding_layout;
    @BindView(R.id.ll_music_player_collapsed)
    LinearLayout ll_music_player_collapsed;

    // page header
    @BindView(R.id.ll_page_header)
    LinearLayout ll_page_header;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.img_header_more)
    ImageView img_header_more;

    // collapse music player
    @BindView(R.id.riv_music_player_collapsed_image)
    RoundedImageView riv_music_player_collapsed_image;
    @BindView(R.id.tv_music_player_collapsed_title)
    TextView tv_music_player_collapsed_title;
    @BindView(R.id.tv_music_player_collapsed_artist)
    TextView tv_music_player_collapsed_artist;

    // extended music player
    @BindView(R.id.riv_extended_music_player_music)
    RoundedImageView riv_extended_music_player_music;
    @BindView(R.id.tv_extended_music_player_title)
    TextView tv_extended_music_player_title;
    @BindView(R.id.tv_extended_music_player_artist)
    TextView tv_extended_music_player_artist;


    @BindView(R.id.img_single_music_play)
    ImageView img_single_music_play;

    private boolean doubleBackToExitPressedOnce = false;
    private Unbinder unbinder;
    private DisposableObserver disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setupBottomNavigation();
        ll_page_header.setAlpha(0);
        sliding_layout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                ll_music_player_collapsed.setAlpha(1 - slideOffset);
                ll_page_header.setAlpha(0 + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
            }
        });
        sliding_layout.setFadeOnClickListener(view -> sliding_layout.setPanelState(PanelState.COLLAPSED));
        setupMusicController();
        musicChangeListener();

        LocalHelper.updateResources(this, "fa");
    }

    private void setupMusicController() {
        img_single_music_play.setOnClickListener(v -> {
        });
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


    public void musicChangeListener() {
        disposable = RxBus.getSubject().subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object music) {
                        if (music instanceof Music) {
                            tv_header_title.setText(((Music) music).getTitle());
                            tv_music_player_collapsed_title.setText(((Music) music).getTitle());
                            tv_extended_music_player_title.setText(((Music) music).getTitle());
                            tv_extended_music_player_artist.setText(((Music) music).getArtist_name());
                            tv_music_player_collapsed_artist.setText(((Music) music).getArtist_name());
                            Picasso.get()
                                    .load(((Music) music).getImage())
                                    .placeholder(R.drawable.ic_music_placeholder)
                                    .error(R.drawable.ic_music_placeholder)
                                    .into(riv_extended_music_player_music);
                            Picasso.get()
                                    .load(((Music) music).getImage())
                                    .placeholder(R.drawable.ic_music_placeholder)
                                    .error(R.drawable.ic_music_placeholder)
                                    .into(riv_music_player_collapsed_image);
                            if (sliding_layout.getPanelState() == PanelState.COLLAPSED)
                                sliding_layout.setPanelState(PanelState.EXPANDED);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    @Override
    public void onBackPressed() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }


    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
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
        disposable.dispose();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
