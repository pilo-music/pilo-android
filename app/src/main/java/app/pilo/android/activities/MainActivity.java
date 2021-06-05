package app.pilo.android.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.android.volley.error.VolleyError;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.MusicDraggableVerticalListAdapter;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MusicApi;
import app.pilo.android.api.PlayHistoryApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.event.MusicRelatedEvent;
import app.pilo.android.fragments.BaseFragment;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.MiniMusicPlayerFragment;
import app.pilo.android.fragments.MusicPlayerFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.models.PlayHistory;
import app.pilo.android.services.MusicPlayer.MusicUtils;
import app.pilo.android.services.PlayerService;
import app.pilo.android.utils.Constant;
import app.pilo.android.utils.FragmentHistory;
import app.pilo.android.views.FragNavController;
import app.pilo.android.views.NestedScrollableViewHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.ibrahimsn.lib.SmoothBottomBar;

import static app.pilo.android.services.MusicPlayer.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class MainActivity extends BaseActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    // main activity
    @BindView(R.id.bottom_navigation)
    SmoothBottomBar bottom_navigation;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout sliding_layout;
    @BindView(R.id.list)
    NestedScrollView nestedScrollView;
    @BindView(R.id.rl_main_layout)
    RelativeLayout rl_main_layout;
    @BindView(R.id.ll_tab_layout)
    LinearLayout ll_tab_layout;

    private boolean doubleBackToExitPressedOnce = false;
    private Unbinder unbinder;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private MusicDraggableVerticalListAdapter musicVerticalListAdapter;
    private List<Music> musics;
    private UserSharedPrefManager userSharedPrefManager;
    private ItemTouchHelper itemTouchHelper;

    private boolean mReceiversRegistered = false;
    private boolean mBounded;
    private PlayerService playerService;
    private final Handler mHandler = new Handler();
    private boolean active = false;
    private MusicUtils musicUtils;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT) && active) {
                handleIncomingBroadcast(intent);
            }
        }
    };
    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            playerService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            PlayerService.LocalBinder mLocalBinder = (PlayerService.LocalBinder) service;
            playerService = mLocalBinder.getServerInstance();
            if (playerService != null && active) {
                initPlayerUi();

                MiniMusicPlayerFragment miniMusicPlayerFragment = new MiniMusicPlayerFragment(playerService.getMusicModule());
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_music_mini_player, miniMusicPlayerFragment, null)
                        .commit();

                MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment(playerService.getMusicModule());
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_music_player, musicPlayerFragment, null)
                        .commit();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setupStatusBar();
        setupBottomNavigation();
        fragmentHistory = new FragmentHistory();
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.framelayout)
                .transactionListener(this)
                .rootFragmentListener(this, 4)
                .build();
        switchTab(0);
        musicUtils = new MusicUtils(this);
        musics = new ArrayList<>();
        userSharedPrefManager = new UserSharedPrefManager(this);

        setupSlidingUpPanel();
        handleIncomingBroadcast(getIntent());
        setupPlayerStateListener();
        setupSlidingUpPanel();

    }

    private void setupPlayerStateListener() {
    }

    private void setupSlidingUpPanel() {
        sliding_layout.setScrollableViewHelper(new NestedScrollableViewHelper(new WeakReference<>(nestedScrollView)));
        float tablayout_bottom_margin_collapsed = getResources().getDimension(R.dimen.tabbar_height);
        sliding_layout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_tab_layout.getLayoutParams();
                params.bottomMargin = (int) (-slideOffset * tablayout_bottom_margin_collapsed);
                ll_tab_layout.setLayoutParams(params);

            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_tab_layout.getLayoutParams();
                    params.bottomMargin = 0;
                    ll_tab_layout.setLayoutParams(params);
                }
            }
        });
        sliding_layout.setFadeOnClickListener(view -> sliding_layout.setPanelState(PanelState.COLLAPSED));
    }


    public void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rl_main_layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    private void setupBottomNavigation() {

        bottom_navigation.setOnItemSelectedListener(position -> {
            switch (position) {
                case 0:
                    fragmentHistory.push(0);
                    switchTab(0);
                    break;
                case 1:
                    fragmentHistory.push(1);
                    switchTab(1);
                    break;
                case 2:
                    fragmentHistory.push(2);
                    switchTab(2);
                    break;
                case 3:
                    fragmentHistory.push(3);
                    switchTab(3);
                    break;
            }
            return false;
        });
        bottom_navigation.setOnItemReselectedListener(position -> {
            mNavController.clearStack();
            switch (position) {
                case 0:
                    switchTab(0);
                    break;
                case 1:
                    switchTab(1);
                    break;
                case 2:
                    switchTab(2);
                    break;
                case 3:
                    switchTab(3);
                    break;
            }
        });
    }


    private void switchTab(int position) {
        mNavController.switchTab(position);
        if (sliding_layout.getPanelState() == PanelState.EXPANDED) {
            sliding_layout.setPanelState(PanelState.COLLAPSED);
        }
    }

    private void initPlayerUi() {
        checkPlayerService();
        if (playerService == null) {
            return;
        }
        if (!getCurrentSlug().equals("")) {
            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else {
            musicUtils.findDefaultMusic();
        }
    }

    private void handleIncomingBroadcast(Intent intent) {
        if (intent.getBooleanExtra("notify", false)) {
            initPlayerUi();
        } else if (intent.getBooleanExtra("close", false)) {
            if (active) {
                finish();
            }
        } else if (intent.getBooleanExtra("ended", false)) {
            switch (userSharedPrefManager.getRepeatMode()) {
                case Constant.REPEAT_MODE_NONE: {
                    playerService.getMusicModule().getMusicPlayer().skip(true);
                    break;
                }
                case Constant.REPEAT_MODE_ONE: {
                    playerService.getMusicModule().getMusicPlayer().playTrack(musics, getCurrentSlug());
                    break;
                }
            }
        }
    }


    private void startPlayerService() {
        Intent player_service_intent = new Intent(this, PlayerService.class);
        if (!mBounded || playerService == null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(player_service_intent);
            } else {
                startService(player_service_intent);
            }
            bindService(player_service_intent, mConnection, BIND_AUTO_CREATE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        musics.clear();
        musics.addAll(event.musics);
        playerService.getMusicModule().getMusicPlayer().playTrack(musics, event.slug);
    }


    @Subscribe(priority = 1, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicRelatedEvent event) {
        if (event.slug.equals(getCurrentSlug())) {
            playerService.getMusicModule().getMusicPlayer().togglePlay();
            return;
        }
        playerService.getMusicModule().getMusicPlayer().playTrack(musics, event.slug, true);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingBroadcast(intent);
    }


    @Override
    public void onBackPressed() {
        if (sliding_layout.getPanelState() == PanelState.EXPANDED) {
            sliding_layout.setPanelState(PanelState.COLLAPSED);
        } else {
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
                        bottom_navigation.setItemActiveIndex(position);
                    } else {
                        switchTab(0);
                        bottom_navigation.setItemActiveIndex(0);
                        fragmentHistory.emptyStack();
                    }
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {

    }

//    private void setupMusicControlAndMusicLoading() {
//        LinearLayout ll_extended_music_player_controls = musicPlayerFragment.getView().findViewById(R.id.ll_extended_music_player_controls);
//        LinearLayout ll_music_player_collapsed_controls = miniMusicPlayerFragment.getView().findViewById(R.id.ll_music_player_collapsed_controls);
//
//        LinearLayout ll_extended_music_player_loading = musicPlayerFragment.getView().findViewById(R.id.ll_extended_music_player_loading);
//        LinearLayout ll_music_player_collapsed_loading = miniMusicPlayerFragment.getView().findViewById(R.id.ll_music_player_collapsed_loading);
//
//        LinearLayout riv_music_player_collapsed_image = miniMusicPlayerFragment.getView().findViewById(R.id.riv_music_player_collapsed_image);
//        LinearLayout riv_music_player_collapsed_image_placeholder = miniMusicPlayerFragment.getView().findViewById(R.id.riv_music_player_collapsed_image_placeholder);
//        TextView tv_music_player_collapsed_title = miniMusicPlayerFragment.getView().findViewById(R.id.tv_music_player_collapsed_title);
//        TextView tv_music_player_collapsed_artist = miniMusicPlayerFragment.getView().findViewById(R.id.tv_music_player_collapsed_artist);
//
//        if (musicLoading) {
//            ll_extended_music_player_controls.setVisibility(View.GONE);
//            ll_music_player_collapsed_controls.setVisibility(View.GONE);
//
//            ll_extended_music_player_loading.setVisibility(View.VISIBLE);
//            ll_music_player_collapsed_loading.setVisibility(View.VISIBLE);
//
//            riv_music_player_collapsed_image.setVisibility(View.GONE);
//            riv_music_player_collapsed_image_placeholder.setVisibility(View.VISIBLE);
//            tv_music_player_collapsed_title.setText("");
//            tv_music_player_collapsed_artist.setText("");
//        } else {
//            ll_extended_music_player_controls.setVisibility(View.VISIBLE);
//            ll_music_player_collapsed_controls.setVisibility(View.VISIBLE);
//
//            ll_extended_music_player_loading.setVisibility(View.GONE);
//            ll_music_player_collapsed_loading.setVisibility(View.GONE);
//
//            riv_music_player_collapsed_image_placeholder.setVisibility(View.GONE);
//            riv_music_player_collapsed_image.setVisibility(View.VISIBLE);
//        }
//    }


    private String getCurrentSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }

    private void checkPlayerService() {
        if (!mBounded || playerService == null || playerService.getExpoPlayer() == null) {
            startPlayerService();
        }
    }

    public SlidingUpPanelLayout getSliding_layout() {
        return sliding_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;

        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        mReceiversRegistered = true;
        active = true;
        initPlayerUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        if (mReceiversRegistered) {
            unregisterReceiver(mIntentReceiver);
            mReceiversRegistered = false;
        }
        active = false;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        checkPlayerService();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}


