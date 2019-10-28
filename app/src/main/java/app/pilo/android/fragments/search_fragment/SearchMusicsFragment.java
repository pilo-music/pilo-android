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
import app.pilo.android.adapters.MusicCarouselAdapter;
import app.pilo.android.models.Music;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchMusicsFragment extends Fragment {

    private List<Music> musics;
    @BindView(R.id.rc_search_musics)
    RecyclerView rc_musics;

    public static SearchMusicsFragment instance(List<Music> musics) {
        return new SearchMusicsFragment(musics);
    }

    public SearchMusicsFragment(List<Music> musics) {
        this.musics = musics;
    }


    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_musics, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }


    private void setupRecyclerView() {
        if (rc_musics != null) {
            MusicCarouselAdapter musicsAdapter = new MusicCarouselAdapter(getActivity(), musics);
            rc_musics.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rc_musics.setAdapter(musicsAdapter);
        }
    }
}
