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
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.models.Album;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AlbumsListAdapter extends RecyclerView.Adapter<AlbumsListAdapter.AlbumListAdapterViewHolder> {
    private Context context;
    private List<Album> albums;
    private int viewId = R.layout.item_album;

    public AlbumsListAdapter(WeakReference<Context> context, List<Album> albums) {
        this.context = context.get();
        this.albums = albums;
    }

    public AlbumsListAdapter(WeakReference<Context> context, List<Album> albums, int viewId) {
        this.context = context.get();
        this.albums = albums;
        this.viewId = viewId;
    }


    @NonNull
    @Override
    public AlbumListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(viewId, viewGroup, false);
        return new AlbumListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapterViewHolder holder, final int position) {
        final Album album = albums.get(position);
        holder.tv_album_title.setText(album.getTitle());
        holder.tv_album_artist.setText(album.getArtist().getName());
        Glide.with(context)
                .load(album.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.album_image);
        holder.ll_album_item.setOnClickListener(v -> fragmentJump(album));
    }

    private void fragmentJump(Album album) {
        SingleAlbumFragment fragment = new SingleAlbumFragment(album);
        ((MainActivity) context).pushFragment(fragment);
    }


    @Override
    public int getItemCount() {
        return albums.size();
    }

    class AlbumListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_album_item_title)
        TextView tv_album_title;
        @BindView(R.id.tv_album_item_artist)
        TextView tv_album_artist;
        @BindView(R.id.riv_album_item_image)
        ImageView album_image;
        @BindView(R.id.ll_album_item)
        LinearLayout ll_album_item;

        AlbumListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}