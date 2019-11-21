package app.pilo.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String text;

    public SearchHistory() {
        this.text = "";
    }

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
