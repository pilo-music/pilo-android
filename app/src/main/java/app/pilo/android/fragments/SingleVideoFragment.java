package app.pilo.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import app.pilo.android.R;
import app.pilo.android.activities.VideoPlayerActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.api.VideoApi;
import app.pilo.android.models.SingleVideo;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleVideoFragment extends BaseFragment {
    private VideoApi videoApi;
    private LikeApi likeApi;
    private SingleVideo video;
    private Utils utils;
    private boolean likeProcess = false;

    @BindView(R.id.img_header_back)
    ImageView img_header_back;
    @BindView(R.id.img_header_more)
    ImageView img_header_more;
    @BindView(R.id.tv_header_title)
    TextView tv_header_title;
    @BindView(R.id.riv_single_video_image)
    RoundedImageView videoImage;
    @BindView(R.id.img_single_video_like)
    ImageView img_single_video_like;

    private String slug;
    private String title;
    private String image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_video, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            slug = getArguments().getString("slug");
            title = getArguments().getString("title");
            image = getArguments().getString("image");
        }
        videoApi = new VideoApi(getActivity());
        likeApi = new LikeApi(getActivity());
        utils = new Utils();
        setupViews();
        getDataFromServer();
        return view;
    }

    private void setupViews() {
        tv_header_title.setText(title);
        Glide.with(getActivity())
                .load(image)
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(videoImage);
        img_header_back.setOnClickListener(v -> getActivity().onBackPressed());

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick(R.id.fl_single_video)
    void playVideo() {
        if (!video.getVideo().getVideo480().equals("")) {
            Intent mIntent = new Intent(getActivity(), VideoPlayerActivity.class);
            mIntent.putExtra("url", video.getVideo().getVideo480());
            startActivity(mIntent);
        } else {
            Toast.makeText(getActivity(), getString(R.string.server_connection_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromServer() {
        videoApi.single(slug, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    if (data != null) {
                        video = (SingleVideo) data;
                        setupLikeButton();
                    }
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                new HttpErrorHandler(getActivity());
            }
        });
    }

    private void setupLikeButton() {
        if (video == null) {
            return;
        }

        if (video.isHas_like()) {
            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
        } else {
            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
        }
        img_single_video_like.setVisibility(View.VISIBLE);


        img_single_video_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!video.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(img_single_video_like);
                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                likeApi.like(video.getVideo().getSlug(), "video", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                        } else {
                            video.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_off));
                likeApi.like(video.getVideo().getSlug(), "video", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler(getActivity(), message);
                            img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                        } else {
                            video.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                        img_single_video_like.setImageDrawable(getActivity().getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });

    }


}
