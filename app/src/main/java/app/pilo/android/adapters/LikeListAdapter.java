package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.models.Like;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.LikeListAdapterViewHolder> {
    private Context context;
    private List<Like> likes;
    private LikeApi likeApi;

    public LikeListAdapter(WeakReference<Context> context, List<Like> likes) {
        this.context = context.get();
        this.likes = likes;
        likeApi = new LikeApi(context.get());
    }

    @NonNull
    @Override
    public LikeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_like, viewGroup, false);
        return new LikeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeListAdapterViewHolder holder, final int position) {
        final Like like = likes.get(position);
        String image;
        String title;
        String artist;
        String slug;
        switch (like.getType()) {
            case "music":
                holder.tv_like_label.setText("MP3");
                image = like.getMusic().getImage();
                artist = like.getMusic().getArtist().getName();
                title = like.getMusic().getTitle();
                slug = like.getMusic().getSlug();
                break;
            case "video":
                holder.tv_like_label.setText("MP4");
                image = like.getVideo().getImage();
                artist = like.getVideo().getArtist().getName();
                title = like.getVideo().getTitle();
                slug = like.getVideo().getSlug();
                break;
            case "album":
                holder.tv_like_label.setText("ALBUM");
                image = like.getAlbum().getImage();
                artist = like.getAlbum().getArtist().getName();
                title = like.getAlbum().getTitle();
                slug = like.getAlbum().getSlug();
                break;
            default:
                holder.tv_like_label.setText("PLAYLIST");
                image = like.getPlaylist().getImage();
                artist = "";
                title = like.getPlaylist().getTitle();
                slug = like.getPlaylist().getSlug();
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

        holder.img_like.setOnClickListener(v -> likeApi.like(slug, like.getType(), "remove", new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status){
                    likes.remove(like);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, likes.size());
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {

            }
        }));

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
        @BindView(R.id.img_like)
        ImageView img_like;

        LikeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}