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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.ArtistCarouselAdapter;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Album;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistAdapterViewHolder> {
    private Context context;
    private List<Artist> artists;

    public ArtistsAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistsAdapter.ArtistAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_item, viewGroup, false);
        return new ArtistsAdapter.ArtistAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.ArtistAdapterViewHolder holder, final int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16,16,16,16);
        holder.ll_artist_item.setLayoutParams(params);

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
        }else{
            holder.artist_image.setImageResource(R.drawable.ic_artist_placeholder);
        }
    }

    private void fragmentJump(Artist artist) {
        SingleArtistFragment mFragment = new SingleArtistFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", artist.getSlug());
        mBundle.putString("name", artist.getName());
        mBundle.putString("image", artist.getImage());
        mBundle.putInt("id", artist.getId());
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
        return artists.size();
    }

    class ArtistAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_item_name)
        TextView tv_artist_name;
        @BindView(R.id.ll_artist_item)
        LinearLayout ll_artist_item;
        @BindView(R.id.riv_artist_image)
        RoundedImageView artist_image;

        ArtistAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
