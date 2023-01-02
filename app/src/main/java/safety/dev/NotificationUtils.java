package safety.dev;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.core.util.DebugUtils;

public class NotificationUtils {

    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_1 = "action_1";

    public static void displayNotification(Context context) {

        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.police)
                        .setContentTitle("Safety and Security Services")
                        .setContentText("Click the service you want!")
                        .setContentIntent(action1PendingIntent)
                        .addAction(new NotificationCompat.Action(R.drawable.police,
                                "1", action1PendingIntent))
                        .addAction(new NotificationCompat.Action(R.drawable.hosipital,
                                "2", action1PendingIntent))
                        .addAction(new NotificationCompat.Action(R.drawable.fire,
                                "3", action1PendingIntent))
                        .addAction(new NotificationCompat.Action(R.drawable.women,
                                "4", action1PendingIntent))
                        .setOngoing(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static class NotificationActionService extends IntentService {

        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
           // Log.e(Tag,"Received notification action: " + action);
            if (ACTION_1.equals(action)) {
                Toast.makeText(getApplicationContext(),"This is contactEmergencyContacts!",Toast.LENGTH_LONG).show();

                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }
}
