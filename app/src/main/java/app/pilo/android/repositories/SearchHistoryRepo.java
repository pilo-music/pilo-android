package app.pilo.android.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.pilo.android.db.AppDatabase;
import app.pilo.android.db.SearchHistoryDao;
import app.pilo.android.models.SearchHistory;

public class SearchHistoryRepo {
    private static SearchHistoryRepo instance;
    private SearchHistoryDao searchHistoryDao;

    public static SearchHistoryRepo getInstance(Context context) {
        if (instance == null) {
            instance = new SearchHistoryRepo(context);
        }
        return instance;
    }

    public SearchHistoryRepo(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        searchHistoryDao = database.searchHistoryDao();
    }


    public List<SearchHistory> get() {
        try {
            return new GetItemsAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(SearchHistory searchHistory) {
        SearchHistory result = searchHistoryDao.search(searchHistory.getText());
        if (result != null) {
            searchHistoryDao.delete(searchHistory);
        }
        AsyncTask.execute(() -> searchHistoryDao.insert(searchHistory));
    }

    public void update(SearchHistory searchHistory) {
        AsyncTask.execute(() -> searchHistoryDao.insert(searchHistory));
    }

    public void delete(SearchHistory searchHistory) {
        AsyncTask.execute(() -> searchHistoryDao.delete(searchHistory));
    }


    @SuppressLint("StaticFieldLeak")
    private class GetItemsAsyncTask extends AsyncTask<Void, Void, List<SearchHistory>> {
        @Override
        protected List<SearchHistory> doInBackground(Void... voids) {
            List<SearchHistory> results = new ArrayList<>();
            if (searchHistoryDao.get() != null)
                results = searchHistoryDao.get();
            return results;
        }
    }

}
