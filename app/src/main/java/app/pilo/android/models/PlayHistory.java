package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "play_histories")
public class PlayHistory {
    @PrimaryKey(autoGenerate = true)
    private int anInt;
    @Embedded
    private Music music;

    public PlayHistory() {
        this.music = new Music();
    }


    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }
}
