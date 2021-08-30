package app.pilo.android.utils;

import android.content.Context;
import android.os.Environment;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.util.Random;

import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Download;
import app.pilo.android.models.Music;
import app.pilo.android.services.MusicPlayer.MusicUtils;

public class MusicDownloader {

    public static int download(Context context, Music music, iDownload iDownload) {
        MusicUtils musicUtils = new MusicUtils(context);


        String url = musicUtils.getMp3UrlForStreaming(context, music);
        String downloadQuality = new UserSharedPrefManager(context).getDownloadQuality();
        Download download = new Download();


        if (checkExists(context, music, downloadQuality))
            return 0;

        File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        String path = file.getPath();

        long tsLong = System.currentTimeMillis() / 1000;
        String ts = Long.toString(tsLong);
        Random random = new Random();

        String fileName;
        if (downloadQuality.equals("320")) {
            fileName = ts + random.nextInt(1000) + "-320.mp3";
            download.setPath320(fileName);
        } else {
            fileName = ts + random.nextInt(1000) + "-120.mp3";
            download.setPath128(fileName);
        }


        download.setMusic(music);

        return PRDownloader.download(url, path, fileName)
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

    public static boolean checkExists(Context context, Download download, String quality) {
        if (download == null)
            return false;
        if (quality.equals("320")) {
            if (download.getPath320() != null && !download.getPath320().isEmpty())
                return getFile(context, download.getPath320()).exists();
        } else {
            return getFile(context, download.getPath128()).exists();
        }

        return false;
    }

    public static boolean checkExists(Context context, Music music, String quality) {
        Download download = AppDatabase.getInstance(context).downloadDao().findById(music.getSlug());
        if (download == null)
            return false;

        if (quality.equals("320")) {
            if (download.getPath320() != null && !download.getPath320().isEmpty())
                return getFile(context, download.getPath320()).exists();
        } else {
            return getFile(context, download.getPath128()).exists();
        }

        return false;
    }


    public static File getFile(Context context, String filename) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), filename);
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
