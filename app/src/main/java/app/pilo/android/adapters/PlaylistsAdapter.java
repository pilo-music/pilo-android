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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SinglePlaylistFragment;
import app.pilo.android.models.Playlist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistAdapterViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    private int viewId = R.layout.playlist_item;

    public PlaylistsAdapter(WeakReference<Context> context, List<Playlist> playlists) {
        this.context = context.get();
        this.playlists = playlists;
    }

    public PlaylistsAdapter(WeakReference<Context> context, List<Playlist> playlists, int viewId) {
        this.context = context.get();
        this.playlists = playlists;
        this.viewId = viewId;
    }

    @NonNull
    @Override
    public PlaylistAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(viewId, viewGroup, false);
        return new PlaylistsAdapter.PlaylistAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapterViewHolder holder, final int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16);
        holder.ll_playlist_item.setLayoutParams(params);

        final Playlist playlist = playlists.get(position);
        holder.tv_playlist_title.setText(playlist.getTitle());
        String count = playlist.getMusic_count() + " " + context.getString(R.string.music);
        holder.tv_playlist_item_count.setText(count);
        Glide.with(context)
                .load(playlist.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.playlist_image);
        holder.ll_playlist_item.setOnClickListener(v -> fragmentJump(playlist));
    }

    private void fragmentJump(Playlist playlist) {
        SinglePlaylistFragment mFragment = new SinglePlaylistFragment(playlist);
        ((MainActivity) context).pushFragment(mFragment);
    }


    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlaylistAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_item_title)
        TextView tv_playlist_title;
        @BindView(R.id.tv_playlist_item_count)
        TextView tv_playlist_item_count;
        @BindView(R.id.img_playlist_item_play)
        ImageView img_playlist_play;
        @BindView(R.id.riv_playlist_item_image)
        ImageView playlist_image;
        @BindView(R.id.ll_playlist_item)
        LinearLayout ll_playlist_item;

        PlaylistAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
