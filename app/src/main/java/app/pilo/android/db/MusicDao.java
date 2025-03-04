package app.pilo.android.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import app.pilo.android.models.Music;

@Dao
public interface MusicDao {
    @Query("SELECT * FROM musics")
    List<Music> getAll();

    @Query("SELECT * FROM musics WHERE music_title LIKE :title LIMIT 1")
    Music findByName(String title);

    @Query("SELECT * FROM musics WHERE music_slug LIKE :slug LIMIT 1")
    Music findById(String slug);

    @Query("DELETE FROM musics")
    void nukeTable();

    @Insert
    void insertAll(List<Music> musicList);

    @Update
    void update(Music music);

    @Delete
    void delete(Music music);
}
