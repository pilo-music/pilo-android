package app.pilo.android.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.error.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import app.pilo.android.R;
import app.pilo.android.api.HttpErrorHandler;
import app.pilo.android.api.HttpHandler;
import app.pilo.android.api.SearchApi;
import app.pilo.android.fragments.search_fragment.SearchAlbumFragment;
import app.pilo.android.fragments.search_fragment.SearchArtistsFragment;
import app.pilo.android.fragments.search_fragment.SearchMusicsFragment;
import app.pilo.android.fragments.search_fragment.SearchPlaylistFragment;
import app.pilo.android.fragments.search_fragment.SearchVideosFragment;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Search;
import app.pilo.android.models.SearchHistory;
import app.pilo.android.repositories.SearchHistoryRepo;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager2 viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.img_search_voice)
    ImageView img_search_voice;
    @BindView(R.id.img_search_close)
    ImageView img_search_close;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private UserSharedPrefManager userSharedPrefManager;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        userSharedPrefManager = new UserSharedPrefManager(this);
        et_search.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

    private void initTabLayout(Search search) {

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 5;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1:
                        return SearchArtistsFragment.instance(search.getArtists());
                    case 2:
                        return SearchAlbumFragment.instance(search.getAlbums());
                    case 3:
                        return SearchVideosFragment.instance(search.getVideos());
                    case 4:
                        return SearchPlaylistFragment.instance(search.getPlaylists());
                    default:
                        return SearchMusicsFragment.instance(search.getMusics());
                }
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText(R.string.artist);
                    break;
                case 2:
                    tab.setText(R.string.album);
                    break;
                case 3:
                    tab.setText(R.string.video);
                    break;
                case 4:
                    tab.setText(R.string.playlist);
                    break;
                default:
                    tab.setText(R.string.music);
            }
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tablayout_text, null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }

        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    private void search(String text) {
        progressBar.setVisibility(View.VISIBLE);
        SearchApi searchApi = new SearchApi(this);
        searchApi.get(text, new HttpHandler.RequestHandler() {
            @Override
            public void onGetInfo(Object data, String message, boolean status) {
                if (status) {
                    progressBar.setVisibility(View.INVISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    initTabLayout((Search) data);
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
