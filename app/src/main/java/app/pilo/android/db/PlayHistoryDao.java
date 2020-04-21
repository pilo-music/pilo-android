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
    @Query("SELECT * FROM play_histories")
    List<PlayHistory> getAll();

    @Query("DELETE FROM play_histories")
    void nukeTable();

    @Insert
    void insert(PlayHistory playHistory);

    @Update
    void update(PlayHistory playHistory);

    @Delete
    void delete(PlayHistory playHistory);
}
