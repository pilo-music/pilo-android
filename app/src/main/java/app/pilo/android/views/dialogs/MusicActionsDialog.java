package app.pilo.android.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.databinding.DialogMusicActionsBinding;
import app.pilo.android.fragments.AddToPlaylistFragment;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Music;

public class MusicActionsDialog extends BottomSheetDialogFragment {

    private final Music music;
    private final Context context;
    private boolean showLike = true;
    private boolean showDownload = true;

    public final static String TAG = "MusicActionsDialog";

    private DialogMusicActionsBinding binding;

    public MusicActionsDialog(Context context, Music music) {
        this.context = context;
        this.music = music;
    }

    public MusicActionsDialog(Context context, Music music, boolean showLike, boolean showDownload) {
        this.context = context;
        this.music = music;
        this.showLike = showLike;
        this.showDownload = showDownload;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(requireContext());
        binding = DialogMusicActionsBinding.inflate(inflater);
        View view = binding.getRoot();
        setupViews();

        return view;
    }


    private void setupViews() {
        if (!showLike) {
            binding.piloLike.setVisibility(View.GONE);
        }

        if (!showDownload) {
            binding.llMusicActionsDownload.setVisibility(View.GONE);
        }


        binding.piloDb.setMusic(music);
        binding.piloLike.setMusic(music);
        binding.llMusicActionsDownload.setOnClickListener(view -> binding.piloDb.download());

        binding.llMusicActionsGoToArtist.setOnClickListener(v -> {
            SingleArtistFragment singleArtistFragment = new SingleArtistFragment(music.getArtist());
            ((MainActivity) context).pushFragment(singleArtistFragment);
            ((MainActivity) context).getSliding_layout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            this.dismiss();
        });

        binding.llMusicActionsAddToPlaylist.setOnClickListener(v -> {
            AddToPlaylistFragment addToPlaylistFragment = new AddToPlaylistFragment(music);
            ((MainActivity) context).pushFragment(addToPlaylistFragment);
            ((MainActivity) context).getSliding_layout().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            this.dismiss();
        });


        Glide.with(context)
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.rivMusicActionsImage);

        binding.tvMusicActionsMusic.setText(music.getTitle());
        binding.tvMusicActionsArtist.setText(music.getArtist().getName());


        binding.llMusicActionsShare.setOnClickListener(v -> new ShareCompat.IntentBuilder(context)
                .setType("text/plain")
                .setChooserTitle(music.getTitle())
                .setText(music.getShare_url())
                .startChooser());

    }
}
