package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.pilo.android.models.Playlist;
import app.pilo.android.models.SinglePlaylist;

public class SinglePlaylistFragment extends BaseFragment {
    private Playlist playlist;
    private SinglePlaylist singlePlaylist;

    public SinglePlaylistFragment(Playlist playlist) {
        this.playlist = playlist;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
