package app.pilo.android.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import app.pilo.android.R;
import app.pilo.android.adapters.EditItemTouchHelperCallback;
import app.pilo.android.adapters.MusicDraggableVerticalListAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.OnStartDragListener;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.MusicApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.fragments.BaseFragment;
import app.pilo.android.fragments.BrowserFragment;
import app.pilo.android.fragments.HomeFragment;
import app.pilo.android.fragments.ProfileFragment;
import app.pilo.android.fragments.SearchFragment;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.services.PlayerService;
import app.pilo.android.utils.Constant;
import app.pilo.android.utils.FragmentHistory;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.FragNavController;
import app.pilo.android.views.NestedScrollableViewHelper;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    @BindView(R.id.img_music_player_collapsed_play)
    ImageView img_music_player_collapsed_play;

    // extended music player
    @BindView(R.id.riv_extended_music_player_music)
    RoundedImageView riv_extended_music_player_music;
    @BindView(R.id.tv_extended_music_player_title)
    TextView tv_extended_music_player_title;
    @BindView(R.id.tv_extended_music_player_artist)
    TextView tv_extended_music_player_artist;
    @BindView(R.id.seekbar_music)
    SeekBar player_progress;
    @BindView(R.id.img_single_music_play)
    ImageView img_single_music_play;
    @BindView(R.id.img_single_music_next)
    ImageView img_single_music_next;
    @BindView(R.id.img_single_music_previous)
    ImageView img_single_music_previous;
    @BindView(R.id.rc_music_vertical)
    RecyclerView rc_music_vertical;
    @BindView(R.id.tv_single_music_time)
    TextView tv_single_music_time;
    @BindView(R.id.tv_single_music_duration)
    TextView tv_single_music_duration;
    @BindView(R.id.img_single_music_shuffle)
    ImageView img_single_music_shuffle;
    @BindView(R.id.img_single_music_repeat)
    ImageView img_single_music_repeat;

    private boolean doubleBackToExitPressedOnce = false;
    private Unbinder unbinder;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private int[] mTabIconsSelected = {
            R.drawable.bottom_tab_home,
            R.drawable.bottom_tab_browser,
            R.drawable.bottom_tab_search,
            R.drawable.bottom_tab_profile};
    @BindArray(R.array.tab_name)
    String[] TABS;
    MusicDraggableVerticalListAdapter musicVerticalListAdapter;
    List<Music> musics;
    private UserSharedPrefManager userSharedPrefManager;
    private ItemTouchHelper itemTouchHelper;


    private boolean mReceiversRegistered = false;
    private boolean mBounded, is_seeking;
    private PlayerService playerService;
    private final Handler mHandler = new Handler();
    private boolean active = false;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(PlayerService.CUSTOM_PLAYER_INTENT) && active) {
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
            }
        }
    };


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

        musics = new ArrayList<>();
        userSharedPrefManager = new UserSharedPrefManager(this);

        //todo
        musicVerticalListAdapter = new MusicDraggableVerticalListAdapter(new WeakReference<>(this), musics, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(musicVerticalListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rc_music_vertical);


        rc_music_vertical.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rc_music_vertical.setAdapter(musicVerticalListAdapter);

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

        player_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                is_seeking = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                is_seeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (playerService != null) {
                    playerService.seekTo(seekBar.getProgress());
                }

                is_seeking = false;
            }
        });
        handleIncomingBroadcast(getIntent());

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
        if (sliding_layout.getPanelState() == PanelState.EXPANDED) {
            sliding_layout.setPanelState(PanelState.COLLAPSED);
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


    private void initPlayerUi() {
        if (playerService == null) {
            return;
        }
        setRepeatAndShuffle();
        if (musics.size() == 0) {
            List<Music> items_from_db = AppDatabase.getInstance(MainActivity.this).musicDao().getAll();
            musics.addAll(items_from_db);
            musicVerticalListAdapter.notifyDataSetChanged();
        }
        String current_music_slug = playerService.getCurrent_music_slug();
        if (current_music_slug.equals("")) {
            current_music_slug = new UserSharedPrefManager(MainActivity.this).getActiveMusicSlug();
        }
        if (!current_music_slug.equals("")) {

            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }

            if (playerService.getPlayer() != null && playerService.getPlayer().getPlayWhenReady()) {
                img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
                img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
            } else {
                img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_play));
                img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_play));
            }
            Music music = AppDatabase.getInstance(MainActivity.this).musicDao().findById(current_music_slug);
            if (music != null) {
                tv_music_player_collapsed_title.setText(music.getTitle());
                tv_extended_music_player_title.setText(music.getTitle());
                tv_extended_music_player_artist.setText(music.getArtist().getName());
                tv_music_player_collapsed_artist.setText(music.getArtist().getName());

                Glide.with(MainActivity.this)
                        .load(music.getImage())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(riv_extended_music_player_music);
                Glide.with(MainActivity.this)
                        .load(music.getImage())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(riv_music_player_collapsed_image);
            }
        } else {
            checkActiveMusic();
        }
    }

    private void setRepeatAndShuffle() {
        if (active) {
            switch (userSharedPrefManager.getRepeatMode()) {
                case Constant.REPEAT_MODE_NONE: {
                    img_single_music_repeat.setImageDrawable(getDrawable(R.drawable.ic_repeat_off));
                    break;
                }
                case Constant.REPEAT_MODE_ONE: {
                    img_single_music_repeat.setImageDrawable(getDrawable(R.drawable.ic_repeat_on));
                    break;
                }
            }

            if (userSharedPrefManager.getShuffleMode()) {
                img_single_music_shuffle.setImageDrawable(getDrawable(R.drawable.ic_shuffle_on));
            } else {
                img_single_music_shuffle.setImageDrawable(getDrawable(R.drawable.ic_shuffle_off));
            }
        }
    }

    private void handleIncomingBroadcast(Intent intent) {
        if (intent.getIntExtra("progress", -100) != -100) {
            if (player_progress != null && !is_seeking) {
                player_progress.setMax(intent.getIntExtra("max", 0));
                long elapsed = intent.getIntExtra("progress", 0);
                long remaining = player_progress.getMax();

                long minutes_elapsed = (elapsed / 1000) / 60;
                long seconds_elapsed = ((elapsed / 1000) % 60);

                long minutes_remaining = (remaining / 1000) / 60;
                long seconds_remaining = ((remaining / 1000) % 60);

                String remaining_seconds_string = "" + seconds_remaining;
                String elapsed_seconds_string = "" + seconds_elapsed;
                if (seconds_remaining < 10) {
                    remaining_seconds_string = "0" + seconds_remaining;
                }
                if (seconds_elapsed < 10) {
                    elapsed_seconds_string = "0" + seconds_elapsed;
                }
                tv_single_music_time.setText(minutes_elapsed + ":" + elapsed_seconds_string);
                tv_single_music_duration.setText(minutes_remaining + ":" + remaining_seconds_string);
                player_progress.setProgress(intent.getIntExtra("progress", 0));
            }
        } else if (intent.getBooleanExtra("play", false)) {
            img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
        } else if (intent.getBooleanExtra("pause", false)) {
            img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_play));
            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_play));
            if (playerService != null) {
                playerService.getPlayer().setPlayWhenReady(false);
            }
        } else if (intent.getBooleanExtra("notify", false)) {
            initPlayerUi();
        } else if (intent.getBooleanExtra("close", false)) {
            if (active) {
                finish();
            }
        } else if (intent.getBooleanExtra("ended", false)) {
            switch (userSharedPrefManager.getRepeatMode()) {
                case Constant.REPEAT_MODE_NONE: {
                    img_single_music_next.callOnClick();
                    break;
                }
                case Constant.REPEAT_MODE_ONE: {
                    play_music(new UserSharedPrefManager(MainActivity.this).getActiveMusicSlug(), true, false);
                    break;
                }
            }
        }
    }

    private void checkActiveMusic() {
        if (!userSharedPrefManager.getActiveMusicSlug().equals("")) {
            play_music(userSharedPrefManager.getActiveMusicSlug(), false, false);
        } else {
            MusicApi musicApi = new MusicApi(this);
            musicApi.get(null, new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    if (status) {
                        List<Music> result = (List<Music>) data;
                        EventBus.getDefault().post(new MusicEvent(MainActivity.this, result, result.get(0).getSlug(), false, false));
                    } else {
                        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    }
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            });
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


    public void play_music(String music_slug, boolean play_when_ready, boolean should_load_related_items) {

        if (!mBounded || playerService == null) {
            startPlayerService();
            return;
        }
        setRepeatAndShuffle();

        if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }


        if (play_when_ready) {
            img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_pause));
        } else {
            img_single_music_play.setImageDrawable(getDrawable(R.drawable.ic_play));
            img_music_player_collapsed_play.setImageDrawable(getDrawable(R.drawable.ic_play));
        }

        Music music = AppDatabase.getInstance(MainActivity.this).musicDao().findById(music_slug);

        if (!music.getImage().equals("")) {
            Glide.with(MainActivity.this).setDefaultRequestOptions(new RequestOptions()
                    .placeholder(R.drawable.placeholder_song)).load(music.getImage()).into(riv_music_player_collapsed_image);
        }
        player_progress.setProgress(0);


      /*  if (musicFile != null && playerService != null) {todo play from downloaded file
            Uri uri = Uri.fromFile(musicFile);
            sessionManager.setActiveMusicId(music_id);
            playerService.prepareExoPlayerFromURL(uri, music_id, play_when_ready);
        } else {

            String url = func.getMp3UrlForStreaming(music);
            if (!url.equals("") && playerService != null) {
                sessionManager.setActiveMusicId(music_id);
                playerService.prepareExoPlayerFromURL(Uri.parse(url), music_id, play_when_ready);
            }
        }
*/
        String url = Utils.getMp3UrlForStreaming(MainActivity.this, music);
        if (!url.equals("") && playerService != null) {
            userSharedPrefManager.setActiveMusicSlug(music_slug);
            playerService.prepareExoPlayerFromURL(Uri.parse(url), music_slug, play_when_ready);
        }
        initPlayerUi();


        if (should_load_related_items) {
            //  loadRelatedItems(music.music_id);
        }
    }

    public void play_music(String music_slug, boolean play_when_ready, boolean should_load_related_items, boolean just_update_playlist) {
        initPlayerUi();
    }

    public boolean isPlaying() {
        if (playerService != null && playerService.getPlayer() != null && playerService.getPlayer().getPlayWhenReady()) {
            return true;
        }
        return false;
    }

    public void pause() {
        if (playerService != null && playerService.getPlayer() != null && playerService.getPlayer().getPlayWhenReady()) {
            playerService.togglePlay();
        }
    }

    public void expandPlayer() {
        if (active && sliding_layout != null) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        musics = event.musics;
        initPlayerUi();
        musicVerticalListAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.img_single_music_play, R.id.img_music_player_collapsed_play})
    void img_single_music_play() {
        if (playerService != null && !playerService.getCurrent_music_slug().equals("") && playerService.getPlayer() != null) {
            playerService.togglePlay();
        } else {
            String current_music_slug = new UserSharedPrefManager(MainActivity.this).getActiveMusicSlug();
            if (!current_music_slug.equals("")) {
                EventBus.getDefault().post(new MusicEvent(this, musics, current_music_slug, true, false));
            }
        }
    }

    @OnClick({R.id.img_single_music_previous, R.id.img_music_player_collapsed_prev})
    void img_single_music_previous() {
        if (playerService != null && playerService.getPlayer() != null && ((playerService.getPlayer().getCurrentPosition() * 100) / playerService.getPlayer().getDuration() > 5)) {
            playerService.getPlayer().seekTo(0);
        } else {
            if (musics.size() > 0) {
                int active_index = -1;
                for (int i = 0; i < musics.size(); i++) {
                    if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                        active_index = i;
                    }
                }
                if (active_index != -1 && (active_index - 1) >= 0) {
                    EventBus.getDefault().post(new MusicEvent(this, musics, musics.get(active_index - 1).getSlug(), true, true));
                }
            }
        }
    }

    @OnClick({R.id.img_single_music_next, R.id.img_music_player_collapsed_next})
    void img_single_music_next() {
        if (musics.size() > 0) {
            int active_index = -1;
            for (int i = 0; i < musics.size(); i++) {
                if (musics.get(i).getSlug().equals(userSharedPrefManager.getActiveMusicSlug())) {
                    active_index = i;
                }
            }

            if (userSharedPrefManager.getShuffleMode()) {
                Random random = new Random();
                active_index = random.nextInt(musics.size());
            }


            if (active_index != -1 && (active_index + 1) < musics.size()) {
                EventBus.getDefault().post(new MusicEvent(this, musics, musics.get(active_index + 1).getSlug(), true, true));
            } else if (musics.size() > 0) {
                EventBus.getDefault().post(new MusicEvent(this, musics, musics.get(0).getSlug(), true, true));
            }
        }
    }

    @OnClick(R.id.img_single_music_repeat)
    void img_single_music_repeat() {
        if (userSharedPrefManager.getRepeatMode() == Constant.REPEAT_MODE_NONE)
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_ONE);
        else
            userSharedPrefManager.setRepeatMode(Constant.REPEAT_MODE_NONE);

        setRepeatAndShuffle();
    }

    @OnClick(R.id.img_single_music_shuffle)
    void img_single_music_shuffle() {
        if (userSharedPrefManager.getShuffleMode())
            userSharedPrefManager.setShuffleMode(false);
        else
            userSharedPrefManager.setShuffleMode(true);

        setRepeatAndShuffle();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingBroadcast(intent);
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
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;

        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(PlayerService.CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        mReceiversRegistered = true;
        active = true;

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
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        startPlayerService();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}


