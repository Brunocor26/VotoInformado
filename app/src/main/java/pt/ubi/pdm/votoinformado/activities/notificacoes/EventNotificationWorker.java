package pt.ubi.pdm.votoinformado.activities.notificacoes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class EventNotificationWorker extends Worker {
    //classe que efetivamente mostra a notificação no ecrã do utilizador quando chega a hora que foi agendada pelo NotificationScheduler.
    public EventNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Recupera os dados da notificação
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");

        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "event_channel";

        // Cria canal de notificação (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notificações de Eventos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Cria a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // troca para teu icone
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Mostra a notificação
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }
}
