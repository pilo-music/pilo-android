package app.pilo.android.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.error.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.api.RequestHandler;
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
import app.pilo.android.utils.TypeFace;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    @BindView(R.id.viewpager)
    ViewPager2 viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.auto_complete_search)
    AutoCompleteTextView et_search;
    @BindView(R.id.img_search_voice)
    ImageView img_search_voice;
    @BindView(R.id.img_search_close)
    ImageView img_search_close;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private View view;
    private UserSharedPrefManager userSharedPrefManager;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayAdapter<SearchHistory> at_search_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        userSharedPrefManager = new UserSharedPrefManager(getActivity());
        initSearchView();

        img_search_close.setOnClickListener(v -> {
            et_search.setText("");
            img_search_close.setVisibility(View.GONE);
        });
        img_search_voice.setOnClickListener(v -> promptSpeechInput());


        return view;
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
            Toast.makeText(getActivity(), R.string.speech_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private void initSearchView() {
        List<SearchHistory> searches = SearchHistoryRepo.getInstance(getActivity()).get();
        at_search_adapter = new ArrayAdapter<>(getActivity(), R.layout.search_history_item, R.id.autoCompleteItem, searches);
        et_search.setThreshold(5);
        et_search.setAdapter(at_search_adapter);
        et_search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                et_search.showDropDown();
        });


        et_search.setOnKeyListener((v, keyCode, event) -> {
            try {
                if (et_search.getText().toString().length() > 0) {
                    img_search_close.setVisibility(View.VISIBLE);
                } else {
                    img_search_close.setVisibility(View.GONE);
                }
                search(et_search.getText().toString());
                if (keyCode == KeyEvent.KEYCODE_SEARCH
                        || keyCode == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    search(et_search.getText().toString());
                }
                return false;
            } catch (Exception e) {
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
            TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tablayout_text, null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }

        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    private void search(String text) {
        progressBar.setVisibility(View.VISIBLE);
        SearchApi searchApi = new SearchApi(getActivity());
        searchApi.get(text, new RequestHandler.RequestHandlerWithModel<Search>() {
            @Override
            public void onGetInfo(String status, Search data) {
                if (!status.equals("success")) {
                    Alerter.create(getActivity())
                            .setTitle(R.string.server_connection_error)
                            .setText(R.string.server_connection_message)
                            .setBackgroundColorRes(R.color.colorError)
                            .setTitleTypeface(TypeFace.font(getActivity()))
                            .setTextTypeface(TypeFace.font(getActivity()))
                            .setButtonTypeface(TypeFace.font(getActivity()))
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .show();

                    et_search.setText("");
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    initTabLayout(data);
                }
            }

            @Override
            public void onGetError(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Alerter.create(getActivity())
                        .setTitle(R.string.server_connection_error)
                        .setText(R.string.server_connection_message)
                        .setBackgroundColorRes(R.color.colorError)
                        .setTitleTypeface(TypeFace.font(getActivity()))
                        .setTextTypeface(TypeFace.font(getActivity()))
                        .setButtonTypeface(TypeFace.font(getActivity()))
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }
        });

        SearchHistory searchHistory = new SearchHistory(text);
        SearchHistoryRepo.getInstance(getActivity()).insert(searchHistory);
        at_search_adapter.notifyDataSetChanged();
        et_search.setText("");

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
