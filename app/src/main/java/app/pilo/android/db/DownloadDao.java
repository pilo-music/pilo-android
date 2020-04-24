package app.pilo.android.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.pilo.android.models.Download;

@Dao
public interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY uid DESC LIMIT :count offset :page")
    List<Download> get(int page, int count);

    @Query("SELECT * FROM downloads WHERE music_slug LIKE :slug LIMIT 1")
    Download findById(String slug);

    @Query("DELETE FROM downloads")
    void nukeTable();

    @Insert
    void insert(Download download);

    @Update
    void update(Download download);

    @Delete
    void delete(Download download);
}
