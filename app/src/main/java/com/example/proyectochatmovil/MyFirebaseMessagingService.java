package com.example.proyectochatmovil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Verifica si hay una notificación
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        } else {
            Log.d("FCM", "No hay notificación en el mensaje recibido.");
        }
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "chat_notifications";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Cambia esto al icono correcto de tu app
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Para Android O y versiones superiores, el canal ya debería estar creado en MainActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(String fcmtoken) {
        super.onNewToken(fcmtoken);

        // Guarda el fcmtoken en la base de datos bajo el usuario autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            ref.child("fcmtoken").setValue(fcmtoken).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FCM", "Token guardado en la base de datos");
                } else {
                    Log.e("FCM", "Error al guardar el fcmtoken", task.getException());
                }
            });
        }
    }
}