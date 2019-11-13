package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import app.pilo.android.R;
import app.pilo.android.api.PiloApi;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.LocalHelper;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    // main activity
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout sliding_layout;
    @BindView(R.id.ll_music_player_collapsed)
    LinearLayout ll_music_player_collapsed;

    // page header
    @BindView(R.id.ll_page_header)
    LinearLayout ll_page_header;


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
    @BindView(R.id.img_player_context_menu)
    ImageView img_playerContextMenu;

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
        setupMusicContextMenu();
    }

    private void setupMusicController() {
        img_single_music_play.setOnClickListener(v -> {
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (sliding_layout.getPanelState() == PanelState.EXPANDED){
            sliding_layout.setPanelState(PanelState.COLLAPSED);
        }

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
//        bottomBar.setSelectedItemId(0);
        loadFragment(new HomeFragment(), "MAIN_HOME_FRAGMENT");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new HomeFragment(), "MAIN_HOME_FRAGMENT");
                        break;
                    case 1:
                        loadFragment(new BrowserFragment(), "MAIN_BROWSER_FRAGMENT");
                        break;
                    case 2:
                        loadFragment(new SearchFragment(), "MAIN_SEARCH_FRAGMENT");
                        break;
                    case 3:
                        loadFragment(new ProfileFragment(), "MAIN_PROFILE_FRAGMENT");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


   private void setupMusicContextMenu(){
        img_playerContextMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.setOnMenuItemClickListener(item -> false);
            popup.inflate(R.menu.music_more);
            popup.show();
        });

    }


    public void musicChangeListener() {
        disposable = RxBus.getSubject().subscribeWith(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object music) {
                if (music instanceof Music) {
                    tv_music_player_collapsed_title.setText(((Music) music).getTitle());
                    tv_extended_music_player_title.setText(((Music) music).getTitle());
                    tv_extended_music_player_artist.setText(((Music) music).getArtist_name());
                    tv_music_player_collapsed_artist.setText(((Music) music).getArtist_name());
                    Glide.with(MainActivity.this)
                            .load(((Music) music).getImage())
                            .placeholder(R.drawable.ic_music_placeholder)
                            .error(R.drawable.ic_music_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(riv_extended_music_player_music);
                    Glide.with(MainActivity.this)
                            .load(((Music) music).getImage())
                            .placeholder(R.drawable.ic_music_placeholder)
                            .error(R.drawable.ic_music_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        this.loadFragment(fragment,fragment.getClass().toString());

//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(id, fragment, fragment.toString());
//        ft.addToBackStack(null);
//        ft.commit();
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
