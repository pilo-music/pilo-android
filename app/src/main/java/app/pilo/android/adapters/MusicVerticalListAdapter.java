package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.event.MusicRelatedEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.views.dialogs.MusicActionsDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicVerticalListAdapter extends RecyclerView.Adapter<MusicVerticalListAdapter.MusicCarouselAdapterViewHolder> {
    protected Context context;
    protected List<Music> musics;
    protected boolean loadRelative = false;
    private UserSharedPrefManager userSharedPrefManager;

    public MusicVerticalListAdapter(WeakReference<Context> context, List<Music> musics) {
        this.context = context.get();
        this.musics = musics;
        userSharedPrefManager = new UserSharedPrefManager(context.get());
    }

    public MusicVerticalListAdapter(WeakReference<Context> context, List<Music> musics, boolean loadRelative) {
        this.context = context.get();
        this.musics = musics;
        this.loadRelative = loadRelative;
        userSharedPrefManager = new UserSharedPrefManager(context.get());
    }

    @NonNull
    @Override
    public MusicCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vertical_list_music, viewGroup, false);
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
        } else {
            holder.music_item_image.setVisibility(View.VISIBLE);
            holder.fl_music_vertical_list_item_playing.setVisibility(View.GONE);
        }


        holder.ll_music_vertical.setOnClickListener(v -> {
            if (loadRelative) {
                EventBus.getDefault().post(new MusicRelatedEvent(musics, music.getSlug(), true));
            } else {
                EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true));
            }
        });

        holder.img_music_vertical_list_item_more.setOnClickListener(view -> new MusicActionsDialog(context, music).show(((MainActivity) (context)).getSupportFragmentManager(), MusicActionsDialog.TAG));
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
        @BindView(R.id.img_music_vertical_list_item_more)
        ImageView img_music_vertical_list_item_more;
        @BindView(R.id.riv_music_vertical_list_item_image2)
        ImageView music_item_image2;
        @BindView(R.id.fl_music_vertical_list_item_playing)
        FrameLayout fl_music_vertical_list_item_playing;

        MusicCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
