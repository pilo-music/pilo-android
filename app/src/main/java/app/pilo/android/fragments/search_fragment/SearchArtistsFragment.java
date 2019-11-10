package app.pilo.android.fragments.search_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.ArtistCarouselAdapter;
import app.pilo.android.models.Artist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchArtistsFragment extends Fragment {

    private List<Artist> artists;
    private View view;
    @BindView(R.id.rc_search_artists)
    RecyclerView rc_artists;

    public static SearchArtistsFragment instance(List<Artist> artists) {
        return new SearchArtistsFragment(artists);
    }

    public SearchArtistsFragment(List<Artist> artists) {
        this.artists = artists;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_artists, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (rc_artists != null) {
            ArtistCarouselAdapter artistsAdapter = new ArtistCarouselAdapter(getActivity(), artists);
            rc_artists.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rc_artists.setAdapter(artistsAdapter);
        }
    }
}
