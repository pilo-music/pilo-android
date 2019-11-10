package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Artist;
import app.pilo.android.utils.FragmentSwitcher;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistCarouselAdapter extends RecyclerView.Adapter<ArtistCarouselAdapter.ArtistCarouselAdapterViewHolder> {
    private Context context;
    private List<Artist> artists;

    public ArtistCarouselAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_item, viewGroup, false);
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
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", artist.getSlug());
        mBundle.putString("name", artist.getName());
        mBundle.putString("image", artist.getImage());
        mBundle.putInt("id", artist.getId());
        new FragmentSwitcher(context, new SingleArtistFragment(), mBundle);
    }


    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ArtistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_item_name)
        TextView tv_artist_name;
        @BindView(R.id.ll_artist_item)
        LinearLayout ll_artist_item;
        @BindView(R.id.riv_artist_image)
        RoundedImageView artist_image;

        ArtistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
