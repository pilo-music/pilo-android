package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloads")
public class Download {
    @PrimaryKey(autoGenerate = true)
    private int anInt;
    @ColumnInfo(name = "path_320")
    private String path320;
    @ColumnInfo(name = "path_128")
    private String path128;
    @Embedded
    private Music music;

    public Download() {
        this.path128 = "";
        this.path128 = "";
        this.music = new Music();
    }



    public String getPath320() {
        return path320;
    }

    public void setPath320(String path320) {
        this.path320 = path320;
    }

    public String getPath128() {
        return path128;
    }

    public void setPath128(String path128) {
        this.path128 = path128;
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
