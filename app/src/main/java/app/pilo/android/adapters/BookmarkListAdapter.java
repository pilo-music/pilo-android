package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.BookmarkApi;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.models.Album;
import app.pilo.android.models.Bookmark;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.BookmarkListAdapterViewHolder> {
    private Context context;
    private List<Bookmark> bookmarks;
    private BookmarkApi bookmarkApi;

    public BookmarkListAdapter(WeakReference<Context> context, List<Bookmark> bookmarks) {
        this.context = context.get();
        this.bookmarks = bookmarks;
        bookmarkApi = new BookmarkApi(context.get());
    }

    @NonNull
    @Override
    public BookmarkListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_bookmark_list_item, viewGroup, false);
        return new BookmarkListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkListAdapterViewHolder holder, final int position) {
        final Bookmark bookmark = bookmarks.get(position);
        String image;
        String title;
        String artist;
        String slug;
        switch (bookmark.getType()) {
            case "music":
                holder.tv_bookmark_label.setText("MP3");
                image = bookmark.getMusic().getImage();
                artist = bookmark.getMusic().getArtist().getName();
                title = bookmark.getMusic().getTitle();
                slug = bookmark.getMusic().getSlug();
                break;
            case "video":
                holder.tv_bookmark_label.setText("MP4");
                image = bookmark.getVideo().getImage();
                artist = bookmark.getVideo().getArtist().getName();
                title = bookmark.getVideo().getTitle();
                slug = bookmark.getVideo().getSlug();
                break;
            case "album":
                holder.tv_bookmark_label.setText("ALBUM");
                image = bookmark.getAlbum().getImage();
                artist = bookmark.getAlbum().getArtist().getName();
                title = bookmark.getAlbum().getTitle();
                slug = bookmark.getAlbum().getSlug();
                break;
            default:
                holder.tv_bookmark_label.setText("PLAYLIST");
                image = bookmark.getPlaylist().getImage();
                artist = "";
                title = bookmark.getPlaylist().getTitle();
                slug = bookmark.getPlaylist().getSlug();
                break;
        }

        holder.tv_bookmark_name.setText(title);
        holder.tv_bookmark_artist.setText(artist);
        holder.tv_bookmark_date.setText(bookmark.getCreated_at());

        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.riv_bookmark);


        holder.img_bookmark.setOnClickListener(v -> bookmarkApi.bookmark(slug, bookmark.getType(), "remove", new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    bookmarks.remove(bookmark);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookmarks.size());
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {

            }
        }));
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    class BookmarkListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_bookmark_name)
        TextView tv_bookmark_name;
        @BindView(R.id.tv_bookmark_artist)
        TextView tv_bookmark_artist;
        @BindView(R.id.riv_bookmark)
        RoundedImageView riv_bookmark;
        @BindView(R.id.tv_bookmark_date)
        TextView tv_bookmark_date;
        @BindView(R.id.tv_bookmark_label)
        TextView tv_bookmark_label;
        @BindView(R.id.img_bookmark)
        ImageView img_bookmark;

        BookmarkListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}