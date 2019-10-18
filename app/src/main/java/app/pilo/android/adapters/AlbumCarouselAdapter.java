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

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleAlbumFragment;
import app.pilo.android.models.Album;
import app.pilo.android.utils.FragmentSwitcher;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumCarouselAdapter extends RecyclerView.Adapter<AlbumCarouselAdapter.AlbumCarouselAdapterViewHolder> {
    private Context context;
    private List<Album> albums;

    public AlbumCarouselAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item, viewGroup, false);
        return new AlbumCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumCarouselAdapterViewHolder holder, final int position) {
        final Album album = albums.get(position);
        holder.cv_label.setAlpha(.7f);
        holder.img_album_play.setAlpha(.8f);
        holder.tv_album_title.setText(album.getTitle());
        holder.tv_album_artist.setText(album.getArtist_name());
        Glide.with(context)
                .load(album.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.album_image);
        holder.ll_album_item.setOnClickListener(v -> fragmentJump(album));
    }

    private void fragmentJump(Album album) {
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", album.getSlug());
        mBundle.putString("title", album.getTitle());
        mBundle.putString("artist", album.getArtist_name());
        mBundle.putString("artist_slug", album.getArtist_slug());
        mBundle.putString("image", album.getImage());
        new FragmentSwitcher(context, new SingleAlbumFragment(), mBundle);
    }


    @Override
    public int getItemCount() {
        return albums.size();
    }

    class AlbumCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.cv_label)
        CardView cv_label;

        AlbumCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
