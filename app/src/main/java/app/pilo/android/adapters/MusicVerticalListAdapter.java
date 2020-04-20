package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.MusicActionsDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicVerticalListAdapter extends RecyclerView.Adapter<MusicVerticalListAdapter.MusicCarouselAdapterViewHolder> {
    private Context context;
    private List<Music> musics;
    private LikeApi likeApi;
    private boolean likeProcess = false;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    public MusicVerticalListAdapter(WeakReference<Context> context, List<Music> musics) {
        this.context = context.get();
        this.musics = musics;
        this.likeApi = new LikeApi(context.get());
        utils = new Utils();
        userSharedPrefManager = new UserSharedPrefManager(context.get());
    }

    @NonNull
    @Override
    public MusicCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_vertical_list_item, viewGroup, false);
        return new MusicCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicCarouselAdapterViewHolder holder, final int position) {
        final Music music = musics.get(position);
        holder.tv_music_title.setText(music.getTitle());
        holder.tv_music_artist.setText(music.getArtist().getName());
        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.music_item_image);
        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.music_item_image2);

        if (userSharedPrefManager.getActiveMusicSlug().equals(music.getSlug())) {
            holder.music_item_image.setVisibility(View.GONE);
            holder.fl_music_vertical_list_item_playing.setVisibility(View.VISIBLE);
           if (!((MainActivity) context).isPlaying()){
               holder.img_music_vertical_list_item_icon.setImageDrawable(context.getDrawable(R.drawable.ic_play_icon));
           }
        }

        if (music.isHas_like()) {
            holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
        } else {
            holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
        }

        holder.ll_music_vertical.setOnClickListener(v -> EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true, false)));

        holder.img_music_vertical_list_item_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!music.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(holder.img_music_vertical_list_item_like);
                holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                        } else {
                            music.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                        } else {
                            music.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        holder.img_music_vertical_list_item_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });

        holder.ll_music_vertical.setOnLongClickListener(v -> {
            new MusicActionsDialog(context, music).showDialog();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    static class MusicCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_music_vertical_list_item_music)
        TextView tv_music_title;
        @BindView(R.id.tv_music_vertical_list_item_artist)
        TextView tv_music_artist;
        @BindView(R.id.riv_music_vertical_list_item_image)
        ImageView music_item_image;
        @BindView(R.id.ll_music_vertical)
        LinearLayout ll_music_vertical;
        @BindView(R.id.img_music_vertical_list_item_like)
        ImageView img_music_vertical_list_item_like;
        @BindView(R.id.riv_music_vertical_list_item_image2)
        ImageView music_item_image2;
        @BindView(R.id.fl_music_vertical_list_item_playing)
        FrameLayout fl_music_vertical_list_item_playing;
        @BindView(R.id.img_music_vertical_list_item_icon)
        ImageView img_music_vertical_list_item_icon;

        MusicCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
