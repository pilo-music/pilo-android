package app.pilo.android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "play_histories")
public class PlayHistory {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "type")
    private String type;

    public PlayHistory() {
        this.id = 0;
        this.type = "";
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
