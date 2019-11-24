package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Album;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<Album> albums;
    private Context context;

    public AlbumsListAdapter(Context context, List<Album> albums) {
        this.albums = albums;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == albums.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    public void addItems(List<Album> albums) {
        this.albums.addAll(albums);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.albums.add(new Album());
        notifyItemInserted(albums.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = albums.size() - 1;
        Album item = getItem(position);
        if (item != null) {
            albums.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        albums.clear();
        notifyDataSetChanged();
    }

    Album getItem(int position) {
        return albums.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_album_item_title)
        TextView tv_album_title;
        @BindView(R.id.tv_album_item_artist)
        TextView tv_album_artist;
        @BindView(R.id.img_album_item_play)
        ImageView img_album_play;
        @BindView(R.id.riv_album_item_image)
        ImageView album_image;
        @BindView(R.id.ll_album_item)
        LinearLayout ll_album_item;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            Album album = albums.get(position);

            tv_album_title.setText(album.getTitle());
            tv_album_artist.setText(album.getArtist_name());
            Glide.with(context)
                    .load(album.getImage())
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_music_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(album_image);
        }
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}