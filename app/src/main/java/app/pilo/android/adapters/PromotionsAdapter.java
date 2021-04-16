package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SingleVideoFragment;
import app.pilo.android.models.Promotion;
import app.pilo.android.models.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.VideoAdapterViewHolder> {

    private Context context;
    private List<Promotion> promotions;


    public PromotionsAdapter(WeakReference<Context> context, List<Promotion> promotions) {
        this.context = context.get();
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promotion, viewGroup, false);
        return new VideoAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PromotionsAdapter.VideoAdapterViewHolder viewHolder, int position) {

        Promotion promotion = promotions.get(position);

//        viewHolder.tv_video_item_title.setText(video.getTitle());
//        viewHolder.tv_video_item_artist.setText(video.getArtist().getName());
//        Glide.with(context)
//                .load(video.getImage())
//                .placeholder(R.drawable.ic_video_placeholder)
//                .error(R.drawable.ic_video_placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(viewHolder.riv_video_item_image);
//        viewHolder.ll_video_item.setOnClickListener(v -> fragmentJump(video));
    }


    private void fragmentJump(Video video) {
        SingleVideoFragment mFragment = new SingleVideoFragment(video);
        ((MainActivity) context).pushFragment(mFragment);
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }


    static class VideoAdapterViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.riv_video_item_image)
//        RoundedImageView riv_video_item_image;
//        @BindView(R.id.tv_video_item_title)
//        TextView tv_video_item_title;
//        @BindView(R.id.tv_video_item_artist)
//        TextView tv_video_item_artist;
//        @BindView(R.id.ll_video_item)
//        LinearLayout ll_video_item;

        VideoAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}