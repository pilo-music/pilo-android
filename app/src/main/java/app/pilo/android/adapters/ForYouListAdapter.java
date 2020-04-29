package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.event.MusicEvent;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.ForYou;
import app.pilo.android.models.Music;
import app.pilo.android.views.MusicActionsDialog;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ForYouListAdapter extends RecyclerView.Adapter<ForYouListAdapter.ForYouAdapterViewHolder> {
    private Context context;
    private List<ForYou> forYous;

    public ForYouListAdapter(WeakReference<Context> context, List<ForYou> forYous) {
        this.context = context.get();
        this.forYous = forYous;
    }


    @NonNull
    @Override
    public ForYouAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.for_you_carousel_item, viewGroup, false);
        return new ForYouAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForYouAdapterViewHolder holder, final int position) {
        final ForYou forYou = forYous.get(position);
//        Glide.with(context)
//                .load(music.getImage())
//                .placeholder(R.drawable.ic_music_placeholder)
//                .error(R.drawable.ic_music_placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.music_item_image);


//        if (userSharedPrefManager.getActiveMusicSlug().equals(music.getSlug())) {
//            holder.img_music_item_play.setImageDrawable(context.getDrawable(R.drawable.ic_circle_pause_black));
//        }
//
//        holder.ll_music_item.setOnClickListener(v -> {
//            EventBus.getDefault().post(new MusicEvent(context, musics, music.getSlug(), true, false));
//        });
    }


    @Override
    public int getItemCount() {
        return forYous.size();
    }

    static class ForYouAdapterViewHolder extends RecyclerView.ViewHolder {

        ForYouAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}