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

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SinglePlaylistFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Playlist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistVerticalListAdapter extends RecyclerView.Adapter<PlaylistVerticalListAdapter.PlaylistCarouselAdapterViewHolder> {
    private Context context;
    private List<Playlist> playlists;

    public PlaylistVerticalListAdapter(WeakReference<Context> context, List<Playlist> playlists) {
        this.context = context.get();
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_vertical_list_item, viewGroup, false);
        return new PlaylistCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistCarouselAdapterViewHolder holder, final int position) {
        final Playlist playlist = playlists.get(position);
        holder.tv_playlist_title.setText(playlist.getTitle());
        String count = playlist.getMusic_count() + " " + context.getString(R.string.music);
        holder.tv_playlist_count.setText(count);
        Glide.with(context)
                .load(playlist.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.playlist_item_image);


        holder.ll_playlist_vertical.setOnClickListener(v -> {
            SinglePlaylistFragment fragment = new SinglePlaylistFragment(playlist);
            ((MainActivity) context).pushFragment(fragment);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_vertical_list_item_playlist)
        TextView tv_playlist_title;
        @BindView(R.id.tv_playlist_vertical_list_item_count)
        TextView tv_playlist_count;
        @BindView(R.id.riv_playlist_vertical_list_item_image)
        ImageView playlist_item_image;
        @BindView(R.id.ll_playlist_vertical)
        LinearLayout ll_playlist_vertical;

        PlaylistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
