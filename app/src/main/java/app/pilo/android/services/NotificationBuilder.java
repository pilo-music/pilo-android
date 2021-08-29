package app.pilo.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.jetbrains.annotations.NotNull;

import app.pilo.android.R;
import app.pilo.android.activities.MainActivity;
import app.pilo.android.db.AppDatabase;
import app.pilo.android.helpers.UserSharedPrefManager;
import app.pilo.android.models.Music;

public class NotificationBuilder {

    private final PlayerService context;
    private final NotificationManager notificationManager;
    private final SimpleExoPlayer exoPlayer;
    private final UserSharedPrefManager userSharedPrefManager;

    private static final int NOTIF_ID = 32432;
    private static final String CHANNEL_ID = "my_pilo_channel";
    private static final CharSequence CHANNEL_NAME = "pilo";


    public NotificationBuilder(PlayerService context, SimpleExoPlayer exoPlayer) {
        this.context = context;
        this.exoPlayer = exoPlayer;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        userSharedPrefManager = new UserSharedPrefManager(context);
    }


    public void show() {
        Music music = AppDatabase.getInstance(context).musicDao().findById(currentMusicSlug());

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MEDIA_BUTTON);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        //Intent for Play
        Intent playIntent = new Intent(context, PlayerService.class);
        playIntent.setAction("toggle");
        PendingIntent pplayIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);


        //Intent for next
        Intent nextIntent = new Intent(context, PlayerService.class);
        nextIntent.setAction("next");
        PendingIntent nextPIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        //Intent for previous
        Intent previousIntent = new Intent(context, PlayerService.class);
        previousIntent.setAction("previous");
        PendingIntent ppreviousIntent = PendingIntent.getService(context, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE);

        //Intent for close
        Intent closeIntent = new Intent(context, PlayerService.class);
        closeIntent.setAction("close");
        PendingIntent pcloseIntent = PendingIntent.getService(context, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        int play_pause_icon;
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            play_pause_icon = R.drawable.ic_pause_notification;
        } else {
            play_pause_icon = R.drawable.ic_play_notification;
        }

        if (music != null) {
            String artist_name = music.getArtist() != null ? music.getArtist().getName() : "";
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(Html.fromHtml(music.getTitle()))
                    .setContentText(Html.fromHtml(artist_name))
                    .setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_pilo_logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                    .setOngoing(true)
                    .addAction(R.drawable.ic_previous_notification, "", ppreviousIntent)
                    .addAction(play_pause_icon, "", pplayIntent)
                    .addAction(R.drawable.ic_next_notification, "", nextPIntent)
                    .addAction(R.drawable.ic_close_notification, "", pcloseIntent);


            int largeIconSize = Math.round(64 * context.getResources().getDisplayMetrics().density);
            Glide.with(context)
                    .asBitmap()
                    .load(music.getImage())
                    .override(largeIconSize, largeIconSize)
                    .placeholder(R.drawable.placeholder_song).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                    mNotificationBuilder.setLargeIcon(resource);
                    notificationManager.notify(NOTIF_ID,
                            mNotificationBuilder.build());
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });

            if (Build.VERSION.SDK_INT >= 26) {
                context.startForeground(NOTIF_ID, mNotificationBuilder.build());
            } else {
                notificationManager.notify(NOTIF_ID,
                        mNotificationBuilder.build());
            }

        }
    }


    public void cancelAll() {
        notificationManager.cancelAll();
    }


    private String currentMusicSlug() {
        return userSharedPrefManager.getActiveMusicSlug();
    }
}
