package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.models.Like;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.LikeListAdapterViewHolder> {
    private Context context;
    private List<Like> likes;

    public LikeListAdapter(WeakReference<Context> context, List<Like> likes) {
        this.context = context.get();
        this.likes = likes;
    }

    @NonNull
    @Override
    public LikeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.like_item, viewGroup, false);
        return new LikeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeListAdapterViewHolder holder, final int position) {
        final Like like = likes.get(position);
        String image;
        String title;
        String artist;
        switch (like.getType()) {
            case "music":
                holder.tv_like_label.setText("MP3");
                image = like.getMusic().getImage();
                artist = like.getMusic().getArtist().getName();
                title = like.getMusic().getTitle();
                break;
            case "video":
                holder.tv_like_label.setText("MP4");
                image = like.getVideo().getImage();
                artist = like.getVideo().getArtist().getName();
                title = like.getVideo().getTitle();
                break;
            case "album":
                holder.tv_like_label.setText("ALBUM");
                image = like.getAlbum().getImage();
                artist = like.getAlbum().getArtist().getName();
                title = like.getAlbum().getTitle();
                break;
            default:
                holder.tv_like_label.setText("PLAYLIST");
                image = like.getPlaylist().getImage();
                artist = "";
                title = like.getPlaylist().getTitle();
                break;
        }

        holder.tv_like_name.setText(title);
        holder.tv_like_artist.setText(artist);
        holder.tv_like_date.setText(like.getCreated_at());

        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.riv_like);
    }

    @Override
    public int getItemCount() {
        return likes.size();
    }

    class LikeListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_like_name)
        TextView tv_like_name;
        @BindView(R.id.tv_like_artist)
        TextView tv_like_artist;
        @BindView(R.id.riv_like)
        RoundedImageView riv_like;
        @BindView(R.id.tv_like_date)
        TextView tv_like_date;
        @BindView(R.id.tv_like_label)
        TextView tv_like_label;

        LikeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}