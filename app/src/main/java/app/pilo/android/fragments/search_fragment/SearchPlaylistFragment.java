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
import app.pilo.android.adapters.PlaylistsAdapter;
import app.pilo.android.models.Playlist;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchPlaylistFragment extends Fragment {

    private List<Playlist> playlists;
    private View view;
    @BindView(R.id.rc_search_playlists)
    RecyclerView rc_playlists;


    public static SearchPlaylistFragment instance(List<Playlist> playlists) {
        return new SearchPlaylistFragment(playlists);
    }

    public SearchPlaylistFragment(List<Playlist> playlists){
        this.playlists = playlists;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_playlists, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (rc_playlists != null) {
            PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(getActivity(), playlists);
            rc_playlists.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rc_playlists.setAdapter(playlistsAdapter);
        }
    }
}
