package app.pilo.android.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import app.pilo.android.models.Music;
import app.pilo.android.models.Queue;
import app.pilo.android.models.User;

@Dao
public interface QueueDao {
    @Query("SELECT * FROM queues")
    List<Queue> getAll();

    @Query("SELECT * FROM queues WHERE slug LIKE :slug LIMIT 1")
    Music findById(String slug);

    @Query("DELETE FROM queues")
    void nukeTable();

    @Insert
    void insert(Queue queue);

}
