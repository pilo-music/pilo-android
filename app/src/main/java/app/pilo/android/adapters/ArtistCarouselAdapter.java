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

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Artist;
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
        holder.ll_artist_item.setOnClickListener(v -> {
            fragmentJump(artist);
        });
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

    class ArtistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_item_name)
        TextView tv_artist_name;
        @BindView(R.id.ll_artist_item)
        LinearLayout ll_artist_item;

        ArtistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
