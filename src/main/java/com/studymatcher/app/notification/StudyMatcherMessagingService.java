package com.studymatcher.app.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studymatcher.app.R;
import com.studymatcher.app.StudyMatcherApp;
import com.studymatcher.app.chat.ChatDetailActivity;
import com.studymatcher.app.ui.MainActivity;

/**
 * FCM service — handles push notifications for:
 * - New match found → deep link to Matches tab
 * - New message received → deep link to ChatDetailActivity
 * - Rating prompt → deep link to rating bottom sheet
 * - Session reminder → deep link to MainActivity
 */
public class StudyMatcherMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type    = remoteMessage.getData().get("type");
        String title   = remoteMessage.getData().get("title");
        String body    = remoteMessage.getData().get("body");

        if (type == null) return;

        switch (type) {
            case "NEW_MATCH":
                showNotification(title, body, StudyMatcherApp.CHANNEL_ID_MATCH,
                        mainIntent("matches"), 1001);
                break;
            case "NEW_MESSAGE":
                String convId = remoteMessage.getData().get("conversationId");
                String partnerId = remoteMessage.getData().get("senderId");
                String partnerName = remoteMessage.getData().get("senderName");
                showNotification(title, body, StudyMatcherApp.CHANNEL_ID_MESSAGE,
                        chatIntent(convId, partnerId, partnerName), 1002);
                break;
            case "RATING_PROMPT":
                showNotification(title, body, StudyMatcherApp.CHANNEL_ID_RATING,
                        mainIntent("profile"), 1003);
                break;
            case "SESSION_REMINDER":
                showNotification(title, body, StudyMatcherApp.CHANNEL_ID_REMINDER,
                        mainIntent("home"), 1004);
                break;
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // TODO: send token to Spring Boot API → POST /api/v1/profile/{id}/fcm-token
    }

    private void showNotification(String title, String body, String channelId,
                                  PendingIntent pendingIntent, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, builder.build());
    }

    private PendingIntent mainIntent(String tab) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tab", tab);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent chatIntent(String conversationId, String partnerId, String partnerName) {
        Intent intent = new Intent(this, ChatDetailActivity.class);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("partnerId", partnerId);
        intent.putExtra("partnerName", partnerName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }
}
