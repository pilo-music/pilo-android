package app.pilo.android.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.pilo.android.models.SearchHistory;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * FROM search_histories ORDER BY id DESC LIMIT 3")
    List<SearchHistory> get();

    @Query("SELECT * FROM search_histories WHERE text = :text")
    List<SearchHistory> search(String text);

    @Insert
    void insert(SearchHistory searchHistory);

    @Update
    void update(SearchHistory searchHistory);

    @Delete
    void delete(SearchHistory searchHistory);

    @Query("DELETE FROM search_histories")
    void nukeTable();
}
