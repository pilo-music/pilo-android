package app.pilo.android.adapters.search_adapters;

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

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SinglePlaylistFragment;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistAdapterViewHolder> {
    private Context context;
    private List<Playlist> playlists;

    public PlaylistsAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, viewGroup, false);
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
        holder.cv_label.setAlpha(.7f);
        holder.img_playlist_play.setAlpha(.8f);
        holder.tv_playlist_title.setText(playlist.getTitle());
        holder.tv_playlist_artist.setText(playlist.getArtist_name());
        Glide.with(context)
                .load(playlist.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.playlist_image);
        holder.ll_playlist_item.setOnClickListener(v -> fragmentJump(playlist));
    }

    private void fragmentJump(Playlist playlist) {
        SinglePlaylistFragment mFragment = new SinglePlaylistFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", playlist.getSlug());
        mBundle.putString("title", playlist.getTitle());
        mBundle.putString("artist", playlist.getArtist_name());
        mBundle.putString("artist_slug", playlist.getArtist_slug());
        mBundle.putString("image", playlist.getImage());
        mFragment.setArguments(mBundle);
        switchContent(mFragment);
    }

    private void switchContent(Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.switchContent(R.id.framelayout, fragment);
        }
    }


    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlaylistAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_item_title)
        TextView tv_playlist_title;
        @BindView(R.id.tv_playlist_item_artist)
        TextView tv_playlist_artist;
        @BindView(R.id.img_playlist_item_play)
        ImageView img_playlist_play;
        @BindView(R.id.riv_playlist_item_image)
        ImageView playlist_image;
        @BindView(R.id.ll_playlist_item)
        LinearLayout ll_playlist_item;
        @BindView(R.id.cv_label)
        CardView cv_label;

        PlaylistAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
