package app.pilo.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.adapters.SearchHistoryAdapter;
import app.pilo.android.repositories.SearchHistoryRepo;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends BaseFragment {

    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.rc_history)
    RecyclerView rc_history;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);


        ll_search.setOnClickListener(v -> {
            SearchResultFragment searchResultFragment = new SearchResultFragment(null);
            ((MainActivity) getActivity()).pushFragment(searchResultFragment);
        });
        et_search.setOnClickListener(v -> {
            SearchResultFragment searchResultFragment = new SearchResultFragment(null);
            ((MainActivity) getActivity()).pushFragment(searchResultFragment);
        });
        et_search.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                SearchResultFragment searchResultFragment = new SearchResultFragment(null);
                ((MainActivity) getActivity()).pushFragment(searchResultFragment);
            }
        });

        SearchHistoryAdapter searchHistoryAdapter = new SearchHistoryAdapter(new WeakReference<>(getActivity()), SearchHistoryRepo.getInstance(getActivity()).get());
        rc_history.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc_history.setAdapter(searchHistoryAdapter);


        return view;
    }

}
