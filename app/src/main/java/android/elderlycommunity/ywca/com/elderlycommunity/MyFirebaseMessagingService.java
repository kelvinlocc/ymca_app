package android.elderlycommunity.ywca.com.elderlycommunity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.MainActivity;
import android.elderlycommunity.ywca.com.elderlycommunity.MainActivity.NoticeHistoryActivity;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public final static int REQUEST_CODE = 0;
    final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    // trigger only when the app is in foreground
    // when app is in background, go to launcher activity onstart()
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, NoticeHistoryActivity.class);
        intent.putExtra("noticeContent", remoteMessage.getNotification().getBody());
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /*
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NoticeHistoryActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("YWCA 女青");
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setAutoCancel(true);

        builder.setDefaults(NotificationCompat.DEFAULT_ALL); // must requires VIBRATE permission
        builder.setPriority(NotificationCompat.PRIORITY_MAX); //must give priority to High, Max which will considered as heads-up notification

        builder.setSmallIcon(R.drawable.ic_bell_notify);
        builder.setColor(ContextCompat.getColor(this, R.color.mainBlue));
        //builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_logo));

        builder.setContentIntent(pendingIntent);
        builder.setSound(soundUri);

        long[] vibrate  = {500,200,200,500};
        builder.setVibrate(vibrate);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REQUEST_CODE, builder.build());

        //turnScreenOn(6, this);
    }
    public static void turnScreenOn(final int sec, final Context context)
    {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP, "MyLock");
        wakeLock.acquire(sec*1000);
    }
}
