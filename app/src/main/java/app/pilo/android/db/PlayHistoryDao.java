package app.pilo.android.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import app.pilo.android.models.PlayHistory;

@Dao
public interface PlayHistoryDao {
    @Query("SELECT * FROM play_histories ORDER BY anInt DESC LIMIT :count offset :page")
    List<PlayHistory> get(int page, int count);

    @Query("SELECT * FROM play_histories WHERE music_slug = :slug LIMIT 1")
    PlayHistory search(String slug);

    @Query("DELETE FROM play_histories")
    void nukeTable();

    @Insert
    void insert(PlayHistory playHistory);

    @Update
    void update(PlayHistory playHistory);

    @Delete
    void delete(PlayHistory playHistory);

    @Query("DELETE FROM play_histories")
    void deleteAll();
}
