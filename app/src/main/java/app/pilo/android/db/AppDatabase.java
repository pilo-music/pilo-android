package app.pilo.android.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import app.pilo.android.models.Download;
import app.pilo.android.models.Music;
import app.pilo.android.models.PlayHistory;
import app.pilo.android.models.SearchHistory;
import app.pilo.android.models.User;

@Database(entities = {Music.class, User.class, SearchHistory.class, Download.class, PlayHistory.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "pilo")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void NukeAllTables(Context context) {
        AppDatabase.getInstance(context).musicDao().nukeTable();
        AppDatabase.getInstance(context).userDao().nukeTable();
        AppDatabase.getInstance(context).searchHistoryDao().nukeTable();
        AppDatabase.getInstance(context).playHistoryDao().nukeTable();
        AppDatabase.getInstance(context).downloadDao().nukeTable();
    }

    public abstract MusicDao musicDao();

    public abstract UserDao userDao();

    public abstract SearchHistoryDao searchHistoryDao();

    public abstract DownloadDao downloadDao();

    public abstract PlayHistoryDao playHistoryDao();

}
