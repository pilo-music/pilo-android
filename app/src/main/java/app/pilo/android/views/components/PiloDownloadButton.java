package app.pilo.android.views.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;
import app.pilo.android.utils.MusicDownloader;

public class PiloDownloadButton extends LinearLayout {
    private Music music;
    private final Context context;
    private final UserSharedPrefManager userSharedPrefManager;

    private final ImageView imgSync;
    private final DownloadButtonProgress dbProgress;

    private boolean hasDownloadComplete = false;
    private int fileDownloadId = 0;


    public void setMusic(Music music) {
        this.music = music;
        fileDownloadId= 0;

        if (MusicDownloader.checkExists(context, music, userSharedPrefManager.getDownloadQuality())) {
            imgSync.setEnabled(false);
            imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));
        } else {
            imgSync.setEnabled(true);
            imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_download));
        }
    }

    public PiloDownloadButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pilo_download_button, this, true);
        userSharedPrefManager = new UserSharedPrefManager(context);

        setClickable(true);
        setFocusable(true);

        imgSync = (ImageView) getChildAt(0);
        dbProgress = (DownloadButtonProgress) getChildAt(1);

        imgSync.setOnClickListener(view -> download());
        dbProgress.setOnClickListener(view -> download());
    }

    public void download() {
        if (music == null){
            return;
        }


        if (fileDownloadId != 0) {
            this.cancel();
            return;
        }
        if (((MainActivity) context).getSliding_layout().getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN || music == null) {
            return;
        }

        imgSync.setVisibility(View.GONE);
        dbProgress.setVisibility(View.VISIBLE);
        dbProgress.setIndeterminate();
        hasDownloadComplete = false;
        fileDownloadId = 0;

        fileDownloadId = MusicDownloader.download(context, music, new MusicDownloader.iDownload() {
            @Override
            public void onStartOrResumeListener() {
                dbProgress.setDeterminate();
                dbProgress.setCurrentProgress(0);
            }

            @Override
            public void onProgressListener(Progress progress) {
                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                // Displays the progress bar for the first time.
                dbProgress.setCurrentProgress((int) progressPercent);
            }

            @Override
            public void onPauseListener() {

            }

            @Override
            public void onCancelListener() {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                hasDownloadComplete = true;
                dbProgress.setVisibility(View.GONE);
                imgSync.setEnabled(false);
                imgSync.setVisibility(View.VISIBLE);
                imgSync.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkmark));

                fileDownloadId = 0;
            }
        });

    }

    public void cancel() {
        if (!hasDownloadComplete && fileDownloadId != 0) {
            PRDownloader.cancel(fileDownloadId);
            imgSync.setVisibility(View.VISIBLE);
            dbProgress.setVisibility(View.GONE);
            fileDownloadId = 0;
        }
    }

    @Override
    public boolean callOnClick() {
        this.download();
        return super.callOnClick();
    }
}