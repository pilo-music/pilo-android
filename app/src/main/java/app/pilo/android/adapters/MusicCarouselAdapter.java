package app.pilo.android.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleMusicFragment;
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
        holder.tv_music_title.setText(music.getTitle());
        holder.tv_music_artist.setText(music.getArtist_name());
        holder.ll_music_item.setOnClickListener(v -> {
            fragmentJump(music);
        });
    }

    private void fragmentJump(Music music) {
        SingleMusicFragment mFragment = new SingleMusicFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("slug", music.getSlug());
        mBundle.putString("title", music.getTitle());
        mBundle.putString("artist", music.getArtist_name());
        mBundle.putString("artist_slug", music.getArtist_slug());
        mBundle.putString("image", music.getImage());
        mBundle.putString("url", music.getUrl());
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

        MusicCarouselAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
