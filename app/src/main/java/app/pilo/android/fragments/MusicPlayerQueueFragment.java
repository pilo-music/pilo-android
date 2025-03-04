package app.pilo.android.fragments;
import static app.pilo.android.services.MusicPlayer.Constant.CUSTOM_PLAYER_INTENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.pilo.android.R;
import app.pilo.android.adapters.EditItemTouchHelperCallback;
import app.pilo.android.adapters.MusicDraggableVerticalListAdapter;
import app.pilo.android.databinding.FragmentMusicPlayerQueueBinding;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicPlayer.Constant;

public class MusicPlayerQueueFragment extends Fragment {

    private MusicDraggableVerticalListAdapter musicVerticalListAdapter;
    private ItemTouchHelper itemTouchHelper;
    private List<Music> musics;
    private final Handler mHandler = new Handler();
    private Context context;

    private FragmentMusicPlayerQueueBinding binding;

    public MusicPlayerQueueFragment() {
        super(R.layout.fragment_music_player_queue);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicPlayerQueueBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        musics = AppDatabase.getInstance(context).musicDao().getAll();
        musicVerticalListAdapter = new MusicDraggableVerticalListAdapter(new WeakReference<>(context), musics, viewHolder -> itemTouchHelper.startDrag(viewHolder));
        setupMusicVerticalList();
        setupService();
        return view;
    }


    private void setupMusicVerticalList() {
        if (musicVerticalListAdapter == null) {
            return;
        }
        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(musicVerticalListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.rcMusicVertical);
        binding.rcMusicVertical.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        binding.rcMusicVertical.setAdapter(musicVerticalListAdapter);
    }


    private void setupService() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(CUSTOM_PLAYER_INTENT);
        intentToReceiveFilter.setPriority(999);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null && intent.getAction().equals(CUSTOM_PLAYER_INTENT)) {
                    Bundle extras = intent.getExtras();
                    Set<String> ks = extras.keySet();
                    for (String key : ks) {
                        if (key.equals(Constant.INTENT_NOTIFY)){
                            musicVerticalListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, intentToReceiveFilter, null, mHandler);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        List<Music> newList = new ArrayList<>(event.musics);
        musics.clear();
        musics.addAll(newList);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
