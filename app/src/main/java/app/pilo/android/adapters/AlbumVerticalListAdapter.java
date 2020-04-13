package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumVerticalListAdapter extends RecyclerView.Adapter<AlbumVerticalListAdapter.AlbumCarouselAdapterViewHolder> {
    private Context context;
    private List<Album> albums;

    public AlbumVerticalListAdapter(WeakReference<Context> context, List<Album> albums) {
        this.context = context.get();
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_vertical_list_item, viewGroup, false);
        return new AlbumCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumCarouselAdapterViewHolder holder, final int position) {
        final Album album = albums.get(position);
        holder.tv_album_title.setText(album.getTitle());
        holder.tv_album_artist.setText(album.getArtist().getName());
        Glide.with(context)
                .load(album.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.album_item_image);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class AlbumCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_album_vertical_list_item_album)
        TextView tv_album_title;
        @BindView(R.id.tv_album_vertical_list_item_artist)
        TextView tv_album_artist;
        @BindView(R.id.riv_album_vertical_list_item_image)
        ImageView album_item_image;

        AlbumCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
