package app.pilo.android.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_histories")
public class SearchHistory {

    @Ignore
    public SearchHistory() {
        this.text = "";
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String text;


    public SearchHistory(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
