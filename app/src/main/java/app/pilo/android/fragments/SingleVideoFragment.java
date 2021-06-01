package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.activities.VideoPlayerActivity;
import app.pilo.android.adapters.VideoVerticalListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.PlayHistoryApi;
import app.pilo.android.api.VideoApi;
import app.pilo.android.models.SingleVideo;
import app.pilo.android.models.Video;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.pilo.android.services.MusicPlayer.CUSTOM_PLAYER_INTENT;

public class SingleVideoFragment extends BaseFragment {
    private VideoApi videoApi;
    private LikeApi likeApi;
    private SingleVideo singleVideo;
    private Video video;
    private Utils utils;
    private View view;
    private boolean likeProcess = false;

    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.riv_single_video_image)
    RoundedImageView videoImage;
    @BindView(R.id.img_single_video_like)
    ImageView img_single_video_like;
    @BindView(R.id.tv_video_vertical_title)
    TextView tv_video_vertical_title;
    @BindView(R.id.rc_video_vertical)
    RecyclerView rc_video_vertical;
    @BindView(R.id.ll_video_vertical)
    LinearLayout ll_video_vertical;

    public SingleVideoFragment(Video video) {
        this.video = video;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_video, container, false);
        ButterKnife.bind(this, view);
        videoApi = new VideoApi(getActivity());
        likeApi = new LikeApi(getActivity());
        utils = new Utils();
        setupViews();
        getDataFromServer();
        return view;
    }

    private void setupViews() {
        tv_header_title.setText(video.getTitle());
        Glide.with(getActivity())
                .load(video.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(videoImage);
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());
        tv_video_vertical_title.setText(getString(R.string.video_related));
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick(R.id.fl_single_video)
    void playVideo() {
        if (!video.getVideo480().equals("")) {
            Intent intent = new Intent();
            intent.setAction(CUSTOM_PLAYER_INTENT);
            intent.putExtra("pause", true);
            getActivity().sendBroadcast(intent);


            PlayHistoryApi playHistoryApi = new PlayHistoryApi(getActivity());
            playHistoryApi.add(video.getSlug(), "video");
            Intent mIntent = new Intent(getActivity(), VideoPlayerActivity.class);
            mIntent.putExtra("url", video.getVideo480());
            startActivity(mIntent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromServer() {
        videoApi.single(video.getSlug(), new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (!checkView()) {
                    return;
                }
                if (status) {
                    if (data != null) {
                        singleVideo = (SingleVideo) data;
                        if (singleVideo.getRelated().size() > 0) {
                            VideoVerticalListAdapter videoVerticalListAdapter = new VideoVerticalListAdapter(new WeakReference<>(getActivity()), singleVideo.getRelated());
                            rc_video_vertical.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                            rc_video_vertical.setAdapter(videoVerticalListAdapter);
                        }
                        setupLikeButton();
                    }
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                if (!checkView()) {
                    return;
                }
                new HttpErrorHandler(getActivity());

            }
        });
    }

    private void setupLikeButton() {
        if (checkView()) {
            return;
        }

        if (singleVideo == null) {
            return;
        }

        if (singleVideo.isHas_like()) {
            img_single_video_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_on));
        } else {
            img_single_video_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_off));
        }
        img_single_video_like.setVisibility(View.VISIBLE);


        img_single_video_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!singleVideo.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_single_video_like);
                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                likeApi.like(video.getSlug(), "video", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (view != null) {
                            if (!status) {
                                new HttpErrorHandler(getActivity(), message);
                                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                            } else {
                                singleVideo.setHas_like(true);
                            }
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        if (view != null) {
                            new HttpErrorHandler(getActivity());
                            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                        }
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                likeApi.like(video.getSlug(), "video", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (view != null) {
                            if (!status) {
                                new HttpErrorHandler(getActivity(), message);
                                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                            } else {
                                singleVideo.setHas_like(false);
                            }
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        if (view != null) {
                            new HttpErrorHandler(getActivity());
                            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                        }
                    }
                });
                likeProcess = false;
            }
        });
    }


    @OnClick(R.id.ll_video_vertical_show_more)
    void ll_video_vertical_show_more() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("related", video.getSlug());
        VideosFragment mFragment = new VideosFragment(params);
        ((MainActivity) getActivity()).pushFragment(mFragment);
    }

}
