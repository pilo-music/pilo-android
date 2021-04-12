package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumMusicGridListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> items;
    private List<Object> filteredArrayList;

    public AlbumMusicGridListAdapter(WeakReference<Context> context, List<Object> items) {
        this.context = context.get();
        this.items = items;
        this.filteredArrayList = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Album)
            return 0;
        else
            return 1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_full_width_album, viewGroup, false);
            return new AlbumAdapterViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_full_width_music, viewGroup, false);
            return new MusicAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 0) {
            AlbumAdapterViewHolder viewHolder = (AlbumAdapterViewHolder) holder;
            final Album album = (Album) items.get(position);

            viewHolder.tv_album_title.setText(album.getTitle());
            viewHolder.tv_album_artist.setText(album.getArtist().getName());
            Glide.with(context)
                    .load(album.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.album_image);
            viewHolder.ll_album_item.setOnClickListener(v -> {
                SingleAlbumFragment fragment = new SingleAlbumFragment(album);
                ((MainActivity) context).pushFragment(fragment);
            });
        } else {
            MusicAdapterViewHolder viewHolder = (MusicAdapterViewHolder) holder;
            final Music music = (Music) items.get(position);
            viewHolder.tv_music_title.setText(music.getTitle());
            viewHolder.tv_music_artist.setText(music.getArtist().getName());
            Glide.with(context)
                    .load(music.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.music_item_image);


            viewHolder.ll_music_item.setOnClickListener(v -> {
                List<Music> musics = new ArrayList<>();
                for (Object item : items) {
                    if (item instanceof Music)
                        musics.add(music);
                }

                EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true, false));
            });

        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MusicAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_music_item_title)
        TextView tv_music_title;
        @BindView(R.id.tv_music_item_artist)
        TextView tv_music_artist;
        @BindView(R.id.riv_music_item_image)
        ImageView music_item_image;
        @BindView(R.id.ll_music_item)
        LinearLayout ll_music_item;

        MusicAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class AlbumAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_album_item_title)
        TextView tv_album_title;
        @BindView(R.id.tv_album_item_artist)
        TextView tv_album_artist;
        @BindView(R.id.riv_album_item_image)
        ImageView album_image;
        @BindView(R.id.ll_album_item)
        LinearLayout ll_album_item;

        AlbumAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
