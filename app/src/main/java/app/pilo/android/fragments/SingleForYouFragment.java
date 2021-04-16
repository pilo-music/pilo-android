package app.pilo.android.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.ForYou;
import app.pilo.android.models.Music;
import app.pilo.android.models.SinglePlaylist;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleForYouFragment extends BaseFragment {
    private SinglePlaylist singlePlaylist;
    private ForYou forYou;
    private MusicVerticalListAdapter musicVerticalListAdapter;
    private UserSharedPrefManager userSharedPrefManager;

    @BindView(R.id.img_for_you)
    ImageView img_for_you;
    @BindView(R.id.tv_for_you_name)
    TextView tv_for_you_name;
    @BindView(R.id.tv_for_you_count)
    TextView tv_for_you_count;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.rc_for_you)
    RecyclerView rc_for_you;
    @BindView(R.id.fab_for_you_shuffle)
    FloatingActionButton fab_for_you_shuffle;


    public SingleForYouFragment(ForYou forYou) {
        this.forYou = forYou;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_you, container, false);
        ButterKnife.bind(this, view);
        userSharedPrefManager = new UserSharedPrefManager(getActivity());
        setupViews();
        getDataFromServer();
        return view;
    }

    private void setupViews() {
        if (forYou.getImage() != null && !forYou.getImage().equals("")) {
            Glide.with(getActivity())
                    .load(forYou.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_for_you);
        } else {
            img_for_you.setImageResource(R.drawable.ic_music_placeholder);
        }
        tv_for_you_name.setText(forYou.getTitle());
        tv_header_title.setText(forYou.getTitle());
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

        if (userSharedPrefManager.getShuffleMode()) {
            fab_for_you_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_for_you_shuffle.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
        } else {
            fab_for_you_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon));
            fab_for_you_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        }

    }

    private void getDataFromServer() {
//        PlaylistApi playlistApi = new PlaylistApi(getActivity());
//        playlistApi.single(forYou.getSlug(), new HttpHandler.RequestHandler() {
//            @Override
//            public void onGetInfo(Object data, String message, boolean status) {
//                if (status) {
//                    if (((SinglePlaylist) data).getMusics() != null) {
//                        tv_for_you_count.setText(((SinglePlaylist) data).getMusics().size() + " " + getActivity().getString(R.string.music));
//                    } else {
//                        tv_for_you_count.setText("0" + " " + getActivity().getString(R.string.music));
//                    }
//
//                    if (sharedPrefManager.getLocal().equals("fa")) {
//                        tv_for_you_name.setTextDirection(View.TEXT_DIRECTION_RTL);
//                    }
//
//                    setupMusic(((SinglePlaylist) data).getMusics());
//                    singlePlaylist = ((SinglePlaylist) data);
//
//                } else {
//                    new HttpErrorHandler(getActivity(), message);
//                }
//            }
//
//            @Override
//            public void onGetError(@Nullable VolleyError error) {
//                new HttpErrorHandler(getActivity());
//            }
//        });
    }

    private void setupMusic(List<Music> musics) {
        if (musics.size() > 0) {
            musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(getActivity()), musics);
            rc_for_you.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rc_for_you.setAdapter(musicVerticalListAdapter);
        }
    }

    @OnClick(R.id.fab_for_you_play)
    void fab_for_you_play() {
        EventBus.getDefault().post(new MusicEvent(getActivity(), singlePlaylist.getMusics(), singlePlaylist.getMusics().get(0).getSlug(), true));
    }

    @OnClick(R.id.fab_for_you_shuffle)
    void fab_for_you_shuffle() {
        if (userSharedPrefManager.getShuffleMode()) {
            userSharedPrefManager.setShuffleMode(false);
            fab_for_you_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon));
            fab_for_you_shuffle.setBackgroundColor(Color.parseColor("#F1F1F1"));
        } else {
            userSharedPrefManager.setShuffleMode(true);
            fab_for_you_shuffle.setImageDrawable(getActivity().getDrawable(R.drawable.ic_shuffle_icon_primery));
            fab_for_you_shuffle.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryLight));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicEvent event) {
        if (musicVerticalListAdapter != null)
            musicVerticalListAdapter.notifyDataSetChanged();

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
}
