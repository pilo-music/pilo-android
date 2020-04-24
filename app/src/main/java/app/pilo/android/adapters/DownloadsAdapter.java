package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Download;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Utils;
import app.pilo.android.views.MusicActionsDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownlandsAdapterViewHolder> {
    private Context context;
    private List<Download> downloads;
    private List<Music> musics;
    private LikeApi likeApi;
    private boolean likeProcess = false;
    private Utils utils;
    private UserSharedPrefManager userSharedPrefManager;

    public DownloadsAdapter(WeakReference<Context> context, List<Download> downloads) {
        this.context = context.get();
        this.downloads = downloads;
        this.likeApi = new LikeApi(context.get());
        utils = new Utils();
        userSharedPrefManager = new UserSharedPrefManager(context.get());

        for (int i = 0; i < downloads.size(); i++) {
            musics.add(downloads.get(i).getMusic());
        }
    }

    @NonNull
    @Override
    public DownlandsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.download_list_item, viewGroup, false);
        return new DownlandsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownlandsAdapterViewHolder holder, final int position) {
        final Music music = downloads.get(position).getMusic();
        final Download downland = downloads.get(position);
        String quality = "";
        if (downland.getPath320() != null && !downland.getPath320().isEmpty()) {
            quality = "320";
        } else {
            quality = "128";
        }


        holder.tv_download_name.setText(music.getTitle());
        holder.tv_download_artist.setText(music.getArtist().getName());
        holder.tv_download_quality.setText(quality);
        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.riv_download);

        if (music.isHas_like()) {
            holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
        } else {
            holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
        }

        holder.ll_download.setOnClickListener(v -> EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true, false)));

        holder.img_download_like.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!music.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(holder.img_download_like);
                holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                        } else {
                            music.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_off));
                likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                        } else {
                            music.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        holder.img_download_like.setImageDrawable(context.getDrawable(R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });

        holder.ll_download.setOnLongClickListener(v -> {
            new MusicActionsDialog(context, music).showDialog();
            return false;
        });
    }

    public void removeItem(int position) {
        downloads.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Download item, int position) {
        downloads.add(position, item);
        notifyItemInserted(position);
    }

    public List<Download> getData() {
        return downloads;
    }

    @Override
    public int getItemCount() {
        return downloads.size();
    }

    static class DownlandsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_download_name)
        TextView tv_download_name;
        @BindView(R.id.tv_download_artist)
        TextView tv_download_artist;
        @BindView(R.id.riv_download)
        ImageView riv_download;
        @BindView(R.id.ll_download)
        LinearLayout ll_download;
        @BindView(R.id.img_download_like)
        ImageView img_download_like;
        @BindView(R.id.tv_download_quality)
        TextView tv_download_quality;

        DownlandsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
