package app.pilo.android.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.error.VolleyError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.adapters.ArtistVerticalListAdapter;
import app.pilo.android.adapters.MusicVerticalListAdapter;
import app.pilo.android.adapters.PlaylistVerticalListAdapter;
import app.pilo.android.adapters.VideoVerticalListAdapter;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Search;
import app.pilo.android.models.SearchHistory;
import app.pilo.android.repositories.SearchHistoryRepo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_search_voice)
    ImageView img_search_voice;
    @BindView(R.id.img_search_close)
    ImageView img_search_close;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.ll_artist_vertical)
    LinearLayout ll_artist_vertical;
    @BindView(R.id.rc_artist_vertical)
    RecyclerView rc_artist_vertical;
    @BindView(R.id.tv_artist_vertical_title)
    TextView tv_artist_vertical_title;

    @BindView(R.id.ll_music_vertical)
    LinearLayout ll_music_vertical;
    @BindView(R.id.rc_music_vertical)
    RecyclerView rc_music_vertical;
    @BindView(R.id.tv_music_vertical_title)
    TextView tv_music_vertical_title;

    @BindView(R.id.ll_album_vertical)
    LinearLayout ll_album_vertical;
    @BindView(R.id.rc_album_vertical)
    RecyclerView rc_album_vertical;
    @BindView(R.id.tv_album_vertical_title)
    TextView tv_album_vertical_title;


    @BindView(R.id.ll_playlist_vertical)
    LinearLayout ll_playlist_vertical;
    @BindView(R.id.rc_playlist_vertical)
    RecyclerView rc_playlist_vertical;
    @BindView(R.id.tv_playlist_vertical_title)
    TextView tv_playlist_vertical_title;


    @BindView(R.id.ll_video_vertical)
    LinearLayout ll_video_vertical;
    @BindView(R.id.rc_video_vertical)
    RecyclerView rc_video_vertical;
    @BindView(R.id.tv_video_vertical_title)
    TextView tv_video_vertical_title;


    private UserSharedPrefManager userSharedPrefManager;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String query = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        userSharedPrefManager = new UserSharedPrefManager(this);
        et_search.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        tv_album_vertical_title.setText(getString(R.string.albums));
        ll_album_vertical.setVisibility(View.GONE);

        tv_artist_vertical_title.setText(getString(R.string.artists));
        ll_artist_vertical.setVisibility(View.GONE);

        tv_music_vertical_title.setText(getString(R.string.musics));
        ll_music_vertical.setVisibility(View.GONE);

        tv_playlist_vertical_title.setText(getString(R.string.playlists));
        ll_playlist_vertical.setVisibility(View.GONE);

        tv_video_vertical_title.setText(getString(R.string.videos));
        ll_video_vertical.setVisibility(View.GONE);


        initSearchView();

        String text = getIntent().getStringExtra("text");
        if (text != null && text.length() > 0) {
            this.et_search.setText(text);
            search(text);
        }

        img_search_close.setOnClickListener(v -> {
            et_search.setText("");
            img_search_close.setVisibility(View.GONE);
        });
        img_search_voice.setOnClickListener(v -> promptSpeechInput());
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if (userSharedPrefManager.getLocal().equals("fa"))
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");
        else
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.search_search_hint);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, R.string.speech_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private void initSearchView() {
        et_search.setOnKeyListener((v, keyCode, event) -> {
            try {
                if (et_search.getText().toString().length() > 0) {
                    img_search_close.setVisibility(View.VISIBLE);
                    search(et_search.getText().toString());
                } else {
                    img_search_close.setVisibility(View.GONE);
                }
                if (keyCode == KeyEvent.KEYCODE_SEARCH
                        || keyCode == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    search(et_search.getText().toString());
                }
                return false;
            } catch (Exception e) {
                Log.e("search", "initSearchView: " + e.getMessage());
                return false;
            }
        });
    }


    private void search(String text) {
        progressBar.setVisibility(View.VISIBLE);
        SearchApi searchApi = new SearchApi(this);

        HashMap<String, Object> params = new HashMap<>();
        params.put("query", text);

        searchApi.get(params, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    query = text;
                    progressBar.setVisibility(View.INVISIBLE);
                    Search search = (Search) data;
                    if (search.getArtists().size() > 0) {
                        ll_artist_vertical.setVisibility(View.VISIBLE);
                        ArtistVerticalListAdapter artistVerticalListAdapter = new ArtistVerticalListAdapter(new WeakReference<>(SearchActivity.this), search.getArtists());
                        rc_artist_vertical.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
                        rc_artist_vertical.setAdapter(artistVerticalListAdapter);
                    } else {
                        ll_artist_vertical.setVisibility(View.GONE);
                    }

                    if (search.getMusics().size() > 0) {
                        ll_music_vertical.setVisibility(View.VISIBLE);
                        MusicVerticalListAdapter musicVerticalListAdapter = new MusicVerticalListAdapter(new WeakReference<>(SearchActivity.this), search.getMusics());
                        rc_music_vertical.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
                        rc_music_vertical.setAdapter(musicVerticalListAdapter);
                    } else {
                        ll_music_vertical.setVisibility(View.GONE);
                    }


                    if (search.getAlbums().size() > 0) {
                        ll_album_vertical.setVisibility(View.VISIBLE);
                        AlbumsListAdapter albumsListAdapter = new AlbumsListAdapter(new WeakReference<>(SearchActivity.this), search.getAlbums());
                        rc_album_vertical.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
                        rc_album_vertical.setAdapter(albumsListAdapter);
                    } else {
                        ll_album_vertical.setVisibility(View.GONE);
                    }

                    if (search.getPlaylists().size() > 0) {
                        ll_playlist_vertical.setVisibility(View.VISIBLE);
                        PlaylistVerticalListAdapter playlistVerticalListAdapter = new PlaylistVerticalListAdapter(new WeakReference<>(SearchActivity.this), search.getPlaylists());
                        rc_playlist_vertical.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
                        rc_playlist_vertical.setAdapter(playlistVerticalListAdapter);
                    } else {
                        ll_playlist_vertical.setVisibility(View.GONE);
                    }

                    if (search.getVideos().size() > 0) {
                        ll_video_vertical.setVisibility(View.VISIBLE);
                        VideoVerticalListAdapter videoVerticalListAdapter = new VideoVerticalListAdapter(new WeakReference<>(SearchActivity.this), search.getVideos());
                        rc_video_vertical.setLayoutManager(new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false));
                        rc_video_vertical.setAdapter(videoVerticalListAdapter);
                    } else {
                        ll_video_vertical.setVisibility(View.GONE);
                    }


                } else {
                    new HttpErrorHandler(SearchActivity.this, message);
                }
            }

            @Override
            public void onGetError(@Nullable VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                new HttpErrorHandler(SearchActivity.this);
            }
        });

        SearchHistory searchHistory = new SearchHistory(text);
        SearchHistoryRepo.getInstance(this).insert(searchHistory);
    }


    @OnClick(R.id.tv_artist_vertical_show_more)
    void artistShowMore() {
        goToSingle("artist");
    }

    @OnClick(R.id.tv_music_vertical_show_more)
    void musicShowMore() {
        goToSingle("music");
    }

    @OnClick(R.id.tv_album_vertical_show_more)
    void albumShowMore() {
        goToSingle("album");
    }

    @OnClick(R.id.tv_playlist_vertical_show_more)
    void playlistShowMore() {
        goToSingle("playlist");
    }

    @OnClick(R.id.tv_video_vertical_show_more)
    void videoShowMore() {
        goToSingle("video");
    }


    private void goToSingle(String type) {
        Intent intent = new Intent(SearchActivity.this, SingleSearchActivity.class);
        intent.putExtra("query", query);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                et_search.setText(result.get(0));
                search(result.get(0));
            }
        }
    }
}
