package app.pilo.android;

import android.app.Application;
import android.content.res.Configuration;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import org.jetbrains.annotations.NotNull;


public class Pilo extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        // Required initialization logic here!`
    }

}