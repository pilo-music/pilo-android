package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ArtistsListAdapter extends RecyclerView.Adapter<ArtistsListAdapter.ArtistCarouselAdapterViewHolder> {
    private Context context;
    private List<Artist> artists;
    private int viewId = R.layout.artist_item;

    public ArtistsListAdapter(WeakReference<Context> context, List<Artist> artists) {
        this.context = context.get();
        this.artists = artists;
    }

    public ArtistsListAdapter(WeakReference<Context> context, List<Artist> artists, int viewId) {
        this.context = context.get();
        this.artists = artists;
        this.viewId = viewId;
    }

    @NonNull
    @Override
    public ArtistCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(viewId, viewGroup, false);
        return new ArtistCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistCarouselAdapterViewHolder holder, final int position) {
        final Artist artist = artists.get(position);
        holder.tv_artist_name.setText(artist.getName());
        holder.ll_artist_item.setOnClickListener(v -> fragmentJump(artist));
        if (!artist.getImage().equals("") && !artist.getImage().equals("null") && !artist.getImage().equals(" ")) {
            Glide.with(context)
                    .load(artist.getImage())
                    .placeholder(R.drawable.ic_artist_placeholder)
                    .error(R.drawable.ic_artist_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.artist_image);
        }
    }

    private void fragmentJump(Artist artist) {
        SingleArtistFragment fragment = new SingleArtistFragment(artist);
        ((MainActivity) context).pushFragment(fragment);
    }


    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_item_name)
        TextView tv_artist_name;
        @BindView(R.id.ll_artist_item)
        LinearLayout ll_artist_item;
        @BindView(R.id.civ_artist_image)
        CircleImageView artist_image;

        ArtistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
