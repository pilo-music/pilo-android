package app.pilo.android.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.abdularis.buttonprogress.DownloadButtonProgress;

import java.io.File;
import java.util.Random;

import app.pilo.android.activities.MainActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Download;
import app.pilo.android.models.Music;

public class MusicDownloader {

    public static void download(Context context, Music music, iDownload iDownload) {

        String url = Utils.getMp3UrlForStreaming(context, music);
        String downloadQuality = new UserSharedPrefManager(context).getDownloadQuality();
        Download download = new Download();


        if (checkExists(context, music, downloadQuality))
            return;

        File file = context.getExternalFilesDir(null);
        String path = file.getPath();

        long tsLong = System.currentTimeMillis() / 1000;
        String ts = Long.toString(tsLong);
        Random random = new Random();

        String fileName = ts + random.nextInt(1000) + ".mp3";


        if (downloadQuality.equals("320")) {
            download.setPath320(path + "/" + fileName);
        } else {
            download.setPath128(path + "/" + fileName);
        }


        download.setMusic(music);

        PRDownloader.download(url, path, fileName)
                .build()
                .setOnStartOrResumeListener(iDownload::onStartOrResumeListener)
                .setOnPauseListener(iDownload::onPauseListener)
                .setOnCancelListener(iDownload::onCancelListener)
                .setOnProgressListener(iDownload::onProgressListener)
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        AppDatabase.getInstance(context).downloadDao().insert(download);
                        iDownload.onComplete();
                    }

                    @Override
                    public void onError(Error error) {
                        iDownload.onError();
                    }
                });
    }

    public static boolean checkExists(Context context, Music music, String quality) {

        Download download = AppDatabase.getInstance(context).downloadDao().findById(music.getSlug());
        if (download == null)
            return false;

        if (quality.equals("320")) {
            if (download.getPath320() != null && !download.getPath320().isEmpty())
                return new File(download.getPath320()).exists();
        } else {
            return new File(download.getPath128()).exists();
        }

        return false;
    }


    public interface iDownload {
        void onStartOrResumeListener();

        void onProgressListener(Progress progress);

        void onPauseListener();

        void onCancelListener();

        void onStart();

        void onError();

        void onComplete();
    }

}
