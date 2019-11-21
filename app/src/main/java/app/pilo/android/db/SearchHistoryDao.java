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
    @Query("SELECT * FROM searchhistory ORDER BY id DESC LIMIT 5")
    List<SearchHistory> get();

    @Insert
    void insert(SearchHistory searchHistory);

    @Update
    void update(SearchHistory searchHistory);

    @Delete
    void delete(SearchHistory searchHistory);
}
