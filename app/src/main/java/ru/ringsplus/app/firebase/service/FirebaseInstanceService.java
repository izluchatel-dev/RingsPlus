package ru.ringsplus.app.firebase.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.core.app.NotificationCompat;
import ru.ringsplus.app.MainActivity;
import ru.ringsplus.app.OrderListActivity;
import ru.ringsplus.app.R;

import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_DAY;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_MONTH;
import static ru.ringsplus.app.utils.CalendarUtils.PUT_PARAM_YEAR;

public class FirebaseInstanceService extends FirebaseMessagingService {

    private String getMessageData(RemoteMessage remoteMessage, String field) {
        if (!remoteMessage.getData().isEmpty()) {
            String dataStr = remoteMessage.getData().get(field);
            if (dataStr == null) {
                dataStr = "";
            }

            return dataStr;
        } else
            return "";
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            String notifyTitle = getMessageData(remoteMessage, "title");
            String notifyBody = getMessageData(remoteMessage, "body");
            String day = getMessageData(remoteMessage, "day");
            String month = getMessageData(remoteMessage, "month");
            String year = getMessageData(remoteMessage, "year");

            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), getString(R.string.receive_notify_switch),
                        notificationManager.IMPORTANCE_DEFAULT);

                String NOTIFICATION_CHANNEL_DESC = "RingPlusChannel";
                notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESC);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[] {0, 1000, 500, 1000});

                notificationManager.createNotificationChannel(notificationChannel);
            }

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PUT_PARAM_DAY, Integer.valueOf(day));
            intent.putExtra(PUT_PARAM_MONTH, Integer.valueOf(month));
            intent.putExtra(PUT_PARAM_YEAR, Integer.valueOf(year));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                    getString(R.string.default_notification_channel_id));

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setSound(defaultSoundUri)
                    .setContentTitle(notifyTitle)
                    .setContentText(notifyBody)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }
}
