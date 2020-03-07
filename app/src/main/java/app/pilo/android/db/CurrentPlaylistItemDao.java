package app.pilo.android.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import app.pilo.android.models.CurrentPlaylistItem;

@Dao
public interface CurrentPlaylistItemDao {
    @Query("SELECT * FROM current_playlist_item")
    List<CurrentPlaylistItem> getAll();

    @Query("SELECT * FROM current_playlist_item WHERE slug = :slug LIMIT 1")
    CurrentPlaylistItem findByName(String slug);

    @Query("SELECT * FROM current_playlist_item WHERE id LIKE :id LIMIT 1")
    CurrentPlaylistItem findById(int id);

    @Query("DELETE FROM current_playlist_item")
    void nukeTable();

    @Insert
    void insert(CurrentPlaylistItem currentPlaylistItem);

    @Update
    void update(CurrentPlaylistItem currentPlaylist);

    @Delete
    void delete(CurrentPlaylistItem currentPlaylist);
}
