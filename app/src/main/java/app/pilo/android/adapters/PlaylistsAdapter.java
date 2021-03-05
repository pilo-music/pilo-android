package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

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
        return new PlaylistAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapterViewHolder holder, final int position) {
        final Playlist playlist = playlists.get(position);
        holder.tv_playlist_title.setText(playlist.getTitle());
        String count = playlist.getMusic_count() + " " + context.getString(R.string.music);
        holder.tv_playlist_item_count.setText(count);

        if (!playlist.getImage().isEmpty()) {
            Glide.with(context)
                    .load(playlist.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.riv_playlist_item_image);
        } else {
            if (!playlist.getImage_one().isEmpty() && playlist.getImage_two().isEmpty() && playlist.getImage_three().isEmpty() && playlist.getImage_four().isEmpty()) {
                Glide.with(context)
                        .load(playlist.getImage_one())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.riv_playlist_item_image);
            } else {
                holder.riv_playlist_item_image.setVisibility(View.GONE);
                holder.rl_playlist_item_image_layout.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(playlist.getImage_one())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.riv_playlist_item_image_one);
                Glide.with(context)
                        .load(playlist.getImage_two())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.riv_playlist_item_image_two);
                Glide.with(context)
                        .load(playlist.getImage_three())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.riv_playlist_item_image_three);
                Glide.with(context)
                        .load(playlist.getImage_four())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.riv_playlist_item_image_four);

            }

        }


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

    static class PlaylistAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_item_title)
        TextView tv_playlist_title;
        @BindView(R.id.tv_playlist_item_count)
        TextView tv_playlist_item_count;
        @BindView(R.id.ll_playlist_item)
        LinearLayout ll_playlist_item;
        @BindView(R.id.riv_playlist_item_image)
        RoundedImageView riv_playlist_item_image;
        @BindView(R.id.rl_playlist_item_image_layout)
        RelativeLayout rl_playlist_item_image_layout;
        @BindView(R.id.riv_playlist_item_image_one)
        RoundedImageView riv_playlist_item_image_one;
        @BindView(R.id.riv_playlist_item_image_two)
        RoundedImageView riv_playlist_item_image_two;
        @BindView(R.id.riv_playlist_item_image_three)
        RoundedImageView riv_playlist_item_image_three;
        @BindView(R.id.riv_playlist_item_image_four)
        RoundedImageView riv_playlist_item_image_four;

        PlaylistAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
