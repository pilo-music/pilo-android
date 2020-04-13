package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
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
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.fragments.SinglePlaylistFragment;
import app.pilo.android.models.Album;
import app.pilo.android.models.Playlist;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaylistsListAdapter extends RecyclerView.Adapter<PlaylistsListAdapter.PlaylistListAdapterViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    private int viewId = R.layout.playlist_item;

    public PlaylistsListAdapter(WeakReference<Context> context, List<Playlist> playlists) {
        this.context = context.get();
        this.playlists = playlists;
    }

    public PlaylistsListAdapter(WeakReference<Context> context, List<Playlist> playlists, int viewId) {
        this.context = context.get();
        this.playlists = playlists;
        this.viewId = viewId;
    }


    @NonNull
    @Override
    public PlaylistListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(viewId, viewGroup, false);
        return new PlaylistListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistListAdapterViewHolder holder, final int position) {
        final Playlist playlist = playlists.get(position);

        holder.tv_playlist_title.setText(playlist.getTitle());
        String count = playlist.getMusic_count() + " " + context.getString(R.string.music);
        holder.tv_playlist_count.setText(count);
        Glide.with(context)
                .load(playlist.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.playlist_image);
        holder.ll_playlist_item.setOnClickListener(v -> fragmentJump(playlist));
    }

    private void fragmentJump(Playlist playlist) {
        SinglePlaylistFragment fragment = new SinglePlaylistFragment(playlist);
        ((MainActivity) context).pushFragment(fragment);
    }


    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlaylistListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_item_title)
        TextView tv_playlist_title;
        @BindView(R.id.tv_playlist_item_count)
        TextView tv_playlist_count;
        @BindView(R.id.img_playlist_item_play)
        ImageView img_playlist_play;
        @BindView(R.id.riv_playlist_item_image)
        ImageView  playlist_image;
        @BindView(R.id.ll_playlist_item)
        LinearLayout ll_playlist_item;

        PlaylistListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}