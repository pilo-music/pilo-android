package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Artist;
import app.pilo.android.models.Music;
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
        final Artist music = artists.get(position);
        holder.tv_artist_name.setText(music.getName());
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ArtistCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_artist_item_name)
        TextView tv_artist_name;

        ArtistCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
