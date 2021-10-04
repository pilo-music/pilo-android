package app.pilo.android.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ShareCompat;
import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.LikeApi;
import app.pilo.android.databinding.DialogMusicActionsBinding;
import app.pilo.android.fragments.AddToPlaylistFragment;
import app.pilo.android.fragments.SingleArtistFragment;
import app.pilo.android.models.Music;
import app.pilo.android.utils.Utils;

public class MusicActionsDialog extends BottomSheetDialogFragment {

    private final Music music;
    private final Context context;
    private final LikeApi likeApi;
    private boolean likeProcess = false;
    private final Utils utils;

    public final static String TAG = "MusicActionsDialog";


    private DialogMusicActionsBinding binding;

    public MusicActionsDialog(Context context, Music music) {
        this.context = context;
        this.music = music;
        likeApi = new LikeApi(context);
        utils = new Utils();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(requireContext());
        binding = DialogMusicActionsBinding.inflate(inflater);
        View view = binding.getRoot();

        setupViews();
        setupLike();

        return view;
    }

    private void setupLike() {

        if (music.isHas_like()) {
            binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
        } else {
            binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
        }

        binding.imgMusicActionsLike.setOnClickListener(v -> {
            if (likeProcess)
                return;
            if (!music.isHas_like()) {
                likeProcess = true;
                utils.animateHeartButton(binding.imgMusicActionsLike);
                binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                likeApi.like(music.getSlug(), "music", "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                        } else {
                            music.setHas_like(true);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                    }
                });
                likeProcess = false;
            } else {
                likeProcess = true;
                binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_off));
                likeApi.like(music.getSlug(), "music", "remove", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (!status) {
                            new HttpErrorHandler((MainActivity) context, message);
                            binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                        } else {
                            music.setHas_like(false);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler((MainActivity) context);
                        binding.imgMusicActionsLike.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_like_on));
                    }
                });
                likeProcess = false;
            }
        });
    }


    private void setupViews() {
        binding.piloDb.setMusic(music);
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
