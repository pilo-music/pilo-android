package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.event.MusicRelatedEvent;
import app.pilo.android.models.Music;
import app.pilo.android.views.MusicActionsDialog;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MusicsListAdapter extends RecyclerView.Adapter<MusicsListAdapter.MusicCarouselAdapterViewHolder> {
    private Context context;
    private List<Music> musics;
    private int viewId = R.layout.item_music;
    private boolean loadRelative = false;

    public MusicsListAdapter(WeakReference<Context> context, List<Music> musics) {
        this.context = context.get();
        this.musics = musics;
    }

    public MusicsListAdapter(WeakReference<Context> context, List<Music> musics, int viewId) {
        this.context = context.get();
        this.musics = musics;
        this.viewId = viewId;
    }

    public MusicsListAdapter(WeakReference<Context> context, List<Music> musics, boolean loadRelative) {
        this.context = context.get();
        this.musics = musics;
        this.loadRelative = loadRelative;
    }


    public MusicsListAdapter(WeakReference<Context> context, List<Music> musics, int viewId, boolean loadRelative) {
        this.context = context.get();
        this.musics = musics;
        this.viewId = viewId;
        this.loadRelative = loadRelative;
    }


    @NonNull
    @Override
    public MusicCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(viewId, viewGroup, false);
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

        holder.ll_music_item.setOnLongClickListener(v -> {
            new MusicActionsDialog(context, music).show(((MainActivity) (context)).getSupportFragmentManager(), MusicActionsDialog.TAG);
            return false;
        });

        holder.ll_music_item.setOnClickListener(v -> {
            if (loadRelative) {
                EventBus.getDefault().post(new MusicRelatedEvent(musics, music.getSlug(), true));
            } else {
                EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true));
            }
        });
    }


    @Override
    public int getItemCount() {
        return musics.size();
    }

    static class MusicCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_music_item_title)
        TextView tv_music_title;
        @BindView(R.id.tv_music_item_artist)
        TextView tv_music_artist;
        @BindView(R.id.riv_music_item_image)
        ImageView music_item_image;
        @BindView(R.id.ll_music_item)
        LinearLayout ll_music_item;

        MusicCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}