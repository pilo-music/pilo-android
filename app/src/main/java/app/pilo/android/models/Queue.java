package app.pilo.android.models;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "queues")
public class Queue {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @Embedded
    public Music music;

    public Queue() {

    }
}