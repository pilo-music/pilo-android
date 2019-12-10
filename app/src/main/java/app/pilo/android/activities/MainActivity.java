package app.pilo.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import app.pilo.android.R;
import app.pilo.android.fragments.BaseFragment;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Music;
import app.pilo.android.utils.FragmentHistory;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.FragNavController;
import app.pilo.android.views.NestedScrollableViewHelper;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    // main activity
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout sliding_layout;
    @BindView(R.id.ll_music_player_collapsed)
    LinearLayout ll_music_player_collapsed;
    @BindView(R.id.list)
    NestedScrollView nestedScrollView;

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

    private boolean doubleBackToExitPressedOnce = false;
    private Unbinder unbinder;
    private DisposableObserver disposable;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private int[] mTabIconsSelected = {
            R.drawable.bottom_tab_home,
            R.drawable.bottom_tab_browser,
            R.drawable.bottom_tab_search,
            R.drawable.bottom_tab_profile};
    @BindArray(R.array.tab_name)
    String[] TABS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initTab();
        fragmentHistory = new FragmentHistory();
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.framelayout)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();
        switchTab(0);
        setupBottomNavigation();

        sliding_layout.setScrollableViewHelper(new NestedScrollableViewHelper(new WeakReference<>(nestedScrollView)));
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
        musicChangeListener();
    }


    private void setupBottomNavigation() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentHistory.push(tab.getPosition());
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mNavController.clearStack();
                switchTab(tab.getPosition());
            }
        });

    }


    private void initTab() {
        if (tabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                tabLayout.addTab(tabLayout.newTab());
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }


    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = view.findViewById(R.id.tab_icon);
        icon.setImageResource(mTabIconsSelected[position]);
        return view;
    }


    private void switchTab(int position) {
        mNavController.switchTab(position);
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
        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {
            if (fragmentHistory.isEmpty()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            } else {
                if (fragmentHistory.getStackSize() > 1) {
                    int position = fragmentHistory.popPrevious();
                    switchTab(position);
                    updateTabSelection(position);
                } else {
                    switchTab(0);
                    updateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }


    private void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = tabLayout.getTabAt(i);
            if (currentTab != i) {
                if (selectedTab != null && selectedTab.getCustomView() != null)
                    selectedTab.getCustomView().setSelected(false);
            } else {
                if (selectedTab != null && selectedTab.getCustomView() != null)
                    selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }


    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case FragNavController.TAB1:
                return new HomeFragment();
            case FragNavController.TAB2:
                return new BrowserFragment();
            case FragNavController.TAB3:
                return new SearchFragment();
            case FragNavController.TAB4:
                return new ProfileFragment();
        }
        throw new IllegalStateException("Need to send an index that we know");
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
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {

    }
}


