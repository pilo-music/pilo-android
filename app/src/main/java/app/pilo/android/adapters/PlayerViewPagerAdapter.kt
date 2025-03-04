package app.pilo.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.pilo.android.R
import app.pilo.android.models.Music
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.makeramen.roundedimageview.RoundedImageView

class PlayerViewPagerAdapter(private val context: Context, private val musics: List<Music>) :
    RecyclerView.Adapter<PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_pager_player, parent, false)
        )

    override fun getItemCount(): Int = musics.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        Glide.with(context)
            .load(musics.get(position).getImage())
            .placeholder(R.drawable.ic_music_placeholder)
            .error(R.drawable.ic_music_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.rivPlayerItem)
    }
}


class PagerVH(view: View) : RecyclerView.ViewHolder(view) {
    val rivPlayerItem: RoundedImageView = view.findViewById(R.id.riv_player_item)
}