package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.helpers.RxBus;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicCarouselAdapter extends RecyclerView.Adapter<MusicCarouselAdapter.MusicCarouselAdapterViewHolder> {
    private Context context;
    private List<Music> musics;

    public MusicCarouselAdapter(Context context, List<Music> musics) {
        this.context = context;
        this.musics = musics;
    }

    @NonNull
    @Override
    public MusicCarouselAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, viewGroup, false);
        return new MusicCarouselAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicCarouselAdapterViewHolder holder, final int position) {
        final Music music = musics.get(position);
        holder.cv_label.setAlpha(.7f);
        holder.img_music_item_play.setAlpha(.8f);
        holder.tv_music_title.setText(music.getTitle());
        holder.tv_music_artist.setText(music.getArtist_name());
        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.music_item_image);
        holder.ll_music_item.setOnClickListener(v -> fragmentJump(music));
    }

    private void fragmentJump(Music music) {
        RxBus.getSubject().onNext(music);
    }


    @Override
    public int getItemCount() {
        return musics.size();
    }

    class MusicCarouselAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_music_item_title)
        TextView tv_music_title;
        @BindView(R.id.tv_music_item_artist)
        TextView tv_music_artist;
        @BindView(R.id.img_music_item_play)
        ImageView img_music_item_play;
        @BindView(R.id.riv_music_item_image)
        ImageView music_item_image;
        @BindView(R.id.ll_music_item)
        LinearLayout ll_music_item;
        @BindView(R.id.cv_label)
        CardView cv_label;

        MusicCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
