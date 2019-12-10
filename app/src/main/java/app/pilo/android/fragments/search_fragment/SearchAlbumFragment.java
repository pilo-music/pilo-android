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

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.adapters.AlbumsListAdapter;
import app.pilo.android.models.Album;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAlbumFragment extends Fragment {

    private List<Album> albums;
    @BindView(R.id.rc_search_albums)
    RecyclerView rc_albums;


    public static SearchAlbumFragment instance(List<Album> albums) {
        return new SearchAlbumFragment(albums);
    }

    public SearchAlbumFragment(List<Album> albums) {
        this.albums = albums;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_albums, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (rc_albums != null) {
            AlbumsListAdapter albumsAdapter = new AlbumsListAdapter(new WeakReference<>(getActivity()), albums);
            rc_albums.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rc_albums.setAdapter(albumsAdapter);
        }
    }
}
