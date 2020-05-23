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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;

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
        Glide.with(context)
                .load(forYou.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.riv_for_you_carousel);

        holder.riv_for_you_carousel.setOnClickListener(v -> {
            EventBus.getDefault().post(new MusicEvent(context, forYou.getMusics(), forYou.getMusics().get(0).getSlug(), true, false));
        });
        holder.fab_for_you_carousel_play.setOnClickListener(v -> {
            EventBus.getDefault().post(new MusicEvent(context, forYou.getMusics(), forYou.getMusics().get(0).getSlug(), true, false));
        });
    }


    @Override
    public int getItemCount() {
        return forYous.size();
    }

    static class ForYouAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.riv_for_you_carousel)
        RoundedImageView riv_for_you_carousel;
        @BindView(R.id.fab_for_you_carousel_play)
        FloatingActionButton fab_for_you_carousel_play;

        ForYouAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}