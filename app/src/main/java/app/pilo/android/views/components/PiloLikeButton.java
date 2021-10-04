package app.pilo.android.views.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.android.volley.error.VolleyError;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Utils;

public class PiloLikeButton extends LinearLayout {
    private Music music;
    private final Context context;
    private boolean likeProcess = false;
    private final Utils utils;
    private final LikeApi likeApi;

    private final ImageView imgLike;

    public void setMusic(Music music) {
        this.music = music;
        changeLikeResource();
    }

    public PiloLikeButton(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pilo_like_button, this, true);
        utils = new Utils();
        likeApi = new LikeApi(context);

        setClickable(true);
        setFocusable(true);

        imgLike = (ImageView) getChildAt(0);
        imgLike.setOnClickListener(view -> like());
    }

    void like() {
        if (getSlidingLayout().getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            return;
        }

        if (music == null)
            return;

        if (likeProcess)
            return;


        if (!music.isHas_like()) {
            likeProcess = true;
            utils.animateHeartButton(imgLike);
            imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
            likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    if (status == false) {
                        imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                        new HttpErrorHandler(getMainActivity(), message);
                    } else {
                        Music newMusic = music;
                        newMusic.setHas_like(true);
                        AppDatabase.getInstance(context).musicDao().update(newMusic);
                    }
                    likeProcess=false;
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                    likeProcess=false;
                    new HttpErrorHandler(getMainActivity());
                }
            });
        } else {
            likeProcess = true;
            imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
            likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    if (!status) {
                        imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                        new HttpErrorHandler(getMainActivity(), message);
                    } else {
                        Music newMusic = music;
                        newMusic.setHas_like(false);
                        AppDatabase.getInstance(context).musicDao().update(newMusic);
                    }
                    likeProcess=false;
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                    likeProcess=false;
                    new HttpErrorHandler(getMainActivity());
                }
            });
        }
    }

    @Override
    public boolean callOnClick() {
        this.like();
        return super.callOnClick();
    }

    private MainActivity getMainActivity() {
        return ((MainActivity) context);
    }

    private SlidingUpPanelLayout getSlidingLayout() {
        return ((MainActivity) context).getSliding_layout();
    }

    private void changeLikeResource(){
        if (music.isHas_like()) {
            imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
        } else {
            imgLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
        }
    }
}
