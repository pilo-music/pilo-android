package app.pilo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.fragments.SearchResultFragment;
import app.pilo.android.models.SearchHistory;
import app.pilo.android.repositories.SearchHistoryRepo;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryAdapterViewHolder> {
    private Context context;
    private List<SearchHistory> searchHistories;

    public SearchHistoryAdapter(WeakReference<Context> context, List<SearchHistory> searchHistories) {
        this.context = context.get();
        this.searchHistories = searchHistories;
    }

    @NonNull
    @Override
    public SearchHistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_history, viewGroup, false);
        return new SearchHistoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapterViewHolder holder, final int position) {
        final SearchHistory searchHistory = searchHistories.get(position);
        holder.text.setText(searchHistory.getText());

        holder.text.setOnClickListener(v -> {
            SearchResultFragment searchResultFragment = new SearchResultFragment(searchHistory.getText());
            ((MainActivity) context).pushFragment(searchResultFragment);
        });

        holder.img_delete.setOnClickListener(v -> {
            SearchHistoryRepo.getInstance(context).delete(searchHistory);
            searchHistories.remove(searchHistory);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, searchHistories.size());
        });
    }


    @Override
    public int getItemCount() {
        return searchHistories.size();
    }

    static class SearchHistoryAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.img_delete)
        ImageView img_delete;

        SearchHistoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}