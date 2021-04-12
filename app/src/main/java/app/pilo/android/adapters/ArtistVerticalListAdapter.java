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
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Artist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistVerticalListAdapter extends RecyclerView.Adapter<ArtistVerticalListAdapter.ArtistCarouselAdapterViewHolder> {
    protected Context context;
    protected List<Artist> artists;

    public ArtistVerticalListAdapter(WeakReference<Context> context, List<Artist> artists) {
        this.context = context.get();
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vertical_list_artist, viewGroup, false);
        return new ArtistCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistCarouselAdapterViewHolder holder, final int position) {
        final Artist artist = artists.get(position);
        holder.tv_artist_title.setText(artist.getName());
        String info = context.getString(R.string.musics) + " " + artist.getMusics_count() + " . " + context.getString(R.string.albums) + " " + artist.getAlbum_count() + " . " + context.getString(R.string.videos) + " " + artist.getVideo_count();
        holder.tv_artist_info.setText(info);
        Glide.with(context)
                .load(artist.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.artist_item_image);

        holder.ll_artist_vertical.setOnClickListener(v -> {
            SingleArtistFragment fragment = new SingleArtistFragment(artist);
            if (context instanceof MainActivity)
                ((MainActivity) context).pushFragment(fragment);
            else{
                MainActivity activity = new MainActivity();
                activity.pushFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_vertical_list_item_artist)
        TextView tv_artist_title;
        @BindView(R.id.tv_artist_vertical_list_item_info)
        TextView tv_artist_info;
        @BindView(R.id.riv_artist_vertical_list_item_image)
        ImageView artist_item_image;
        @BindView(R.id.ll_artist_vertical)
        LinearLayout ll_artist_vertical;

        ArtistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
