package app.pilo.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.error.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.PlaylistApi;
import app.pilo.android.models.Music;
import app.pilo.android.models.Playlist;
import app.pilo.android.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddToPlaylistFragment extends BaseFragment {
    private Music music;
    private PlaylistApi playlistApi;

    @BindView(R.id.riv_add_to_playlist_image)
    RoundedImageView riv_add_to_playlist_image;
    @BindView(R.id.tv_add_to_playlist_music)
    TextView tv_add_to_playlist_music;
    @BindView(R.id.tv_add_to_playlist_artist)
    TextView tv_add_to_playlist_artist;
    @BindView(R.id.et_add_to_playlist_name)
    EditText et_add_to_playlist_name;
    @BindView(R.id.ll_add_to_playlist_create)
    LinearLayout ll_add_to_playlist_create;
    @BindView(R.id.progress_bar_add_to_playlist)
    ProgressBar progress_bar_add_to_playlist;
    @BindView(R.id.progress_bar_add_to_playlist_loading)
    ProgressBar progress_bar_add_to_playlist_loading;
    @BindView(R.id.ll_add_to_playlist_container)
    LinearLayout ll_add_to_playlist_container;


    public AddToPlaylistFragment(Music music) {
        this.music = music;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_playlist, container, false);
        ButterKnife.bind(this, view);
        playlistApi = new PlaylistApi(getActivity());
        setupViews();
        setupCreatePlaylist();
        getDataFromServer();

        return view;
    }

    private void setupCreatePlaylist() {
        et_add_to_playlist_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_add_to_playlist_name.getText().toString().length() > 3) {
                    ll_add_to_playlist_create.setEnabled(true);
                    ll_add_to_playlist_create.setBackground(getActivity().getResources().getDrawable(R.drawable.green_round_box_background));
                    ll_add_to_playlist_create.setAlpha(1);
                } else {
                    ll_add_to_playlist_create.setEnabled(false);
                    ll_add_to_playlist_create.setBackground(getActivity().getResources().getDrawable(R.drawable.gray_round_box_background));
                    ll_add_to_playlist_create.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_add_to_playlist_name.getText().toString().length() > 3) {
                    ll_add_to_playlist_create.setEnabled(true);
                    ll_add_to_playlist_create.setBackground(getActivity().getResources().getDrawable(R.drawable.green_round_box_background));
                    ll_add_to_playlist_create.setAlpha(1);
                } else {
                    ll_add_to_playlist_create.setEnabled(false);
                    ll_add_to_playlist_create.setBackground(getActivity().getResources().getDrawable(R.drawable.gray_round_box_background));
                    ll_add_to_playlist_create.setAlpha(0.5f);
                }
            }
        });


        ll_add_to_playlist_create.setOnClickListener(v -> {
            if (et_add_to_playlist_name.getText().toString().length() < 3) {
                et_add_to_playlist_name.setError("");
                return;
            }

            progress_bar_add_to_playlist.setVisibility(View.VISIBLE);
            playlistApi.add(et_add_to_playlist_name.getText().toString(), music, new HttpHandler.RequestHandler() {
                @Override
                public void onGetInfo(Object data, String message, boolean status) {
                    progress_bar_add_to_playlist.setVisibility(View.GONE);
                    if (status) {
                        Alerter.create(getActivity())
                                .setTitle(message)
                                .setTextTypeface(Utils.font(getActivity()))
                                .setTitleTypeface(Utils.font(getActivity()))
                                .setButtonTypeface(Utils.font(getActivity()))
                                .setBackgroundColorRes(R.color.colorGreen)
                                .show();

                        getActivity().onBackPressed();
                    } else
                        new HttpErrorHandler(getActivity(), message);
                }

                @Override
                public void onGetError(@Nullable VolleyError error) {
                    progress_bar_add_to_playlist.setVisibility(View.GONE);
                    new HttpErrorHandler(getActivity());
                }
            });

        });

    }

    private void setupViews() {
        Glide.with(getActivity())
                .load(music.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .error(R.drawable.ic_music_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(riv_add_to_playlist_image);

        tv_add_to_playlist_music.setText(music.getTitle());
        tv_add_to_playlist_artist.setText(music.getArtist().getName());
        ll_add_to_playlist_create.setEnabled(false);
    }

    private void getDataFromServer() {
        progress_bar_add_to_playlist_loading.setVisibility(View.VISIBLE);
        HashMap<String, Object> params = new HashMap<>();
        playlistApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                progress_bar_add_to_playlist_loading.setVisibility(View.GONE);
                if (status) {
                    setupUserPlaylists((List<Playlist>) data);
                } else {
                    new HttpErrorHandler(getActivity(), message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progress_bar_add_to_playlist_loading.setVisibility(View.GONE);
                new HttpErrorHandler(getActivity());
            }
        });
    }

    private void setupUserPlaylists(List<Playlist> playlists) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = ll_add_to_playlist_container;

        for (Playlist playlist : playlists) {
            View view = inflater.inflate(R.layout.playlist_vertical_list_item, parent);
            TextView tv_playlist_title = view.findViewById(R.id.tv_playlist_vertical_list_item_playlist);
            TextView tv_playlist_count = view.findViewById(R.id.tv_playlist_vertical_list_item_count);
            RoundedImageView playlist_item_image = view.findViewById(R.id.riv_playlist_vertical_list_item_image);
            LinearLayout ll_playlist_vertical = view.findViewById(R.id.ll_playlist_vertical);


            tv_playlist_title.setText(playlist.getTitle());
            String count = playlist.getMusic_count() + " " + getString(R.string.music);
            tv_playlist_count.setText(count);

            if (!playlist.getImage().isEmpty()) {
                Glide.with(getActivity())
                        .load(playlist.getImage())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(playlist_item_image);
            } else {
                Glide.with(getActivity())
                        .load(playlist.getImage_one())
                        .placeholder(R.drawable.ic_music_placeholder)
                        .error(R.drawable.ic_music_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(playlist_item_image);
            }

            ll_playlist_vertical.setOnClickListener(v -> {
                ll_add_to_playlist_container.setVisibility(View.GONE);
                progress_bar_add_to_playlist_loading.setVisibility(View.VISIBLE);
                playlistApi.musics(playlist.getSlug(), music, "add", new HttpHandler.RequestHandler() {
                    @Override
                    public void onGetInfo(Object data, String message, boolean status) {
                        if (status) {
                            Alerter.create(getActivity())
                                    .setTitle(message)
                                    .setTextTypeface(Utils.font(getActivity()))
                                    .setTitleTypeface(Utils.font(getActivity()))
                                    .setButtonTypeface(Utils.font(getActivity()))
                                    .setBackgroundColorRes(R.color.colorGreen)
                                    .show();

                            getActivity().onBackPressed();
                        } else {
                            new HttpErrorHandler(getActivity(), message);
                        }
                    }

                    @Override
                    public void onGetError(@Nullable VolleyError error) {
                        new HttpErrorHandler(getActivity());
                    }
                });
            });

        }
    }
}
