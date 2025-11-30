package pt.ubi.pdm.votoinformado.activities.notificacoes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import pt.ubi.pdm.votoinformado.classes.ImportantDate;

public class NotificationScheduler {
    //classe que recebe uma data importante e agenda as notificações (a de 30 minutos antes e a da hora do evento)
    private static final String PREFS_NAME = "events_notifications";
    private static final String KEY_EVENT_PREFIX = "event_";

    /**
     * Agenda notificações para um evento (30 minutos antes e na hora)
     * Somente se ainda não tiver sido agendado
     */
    public static void scheduleEventNotifications(Context context, ImportantDate event) {

        if (event.getDate() == null || event.getTime() == null) return;

        String eventId = buildEventId(event);

        if (isEventScheduled(context, eventId)) {
            // Evento já foi agendado, não faz nada
            return;
        }

        LocalDateTime eventDateTime = LocalDateTime.parse(event.getDate() + "T" + event.getTime());

        // Notificação 30 minutos antes
        scheduleNotification(context,
                eventDateTime.minusMinutes(30),
                event.getTitle(),
                buildMessage(event, true));

        // Notificação na hora
        scheduleNotification(context,
                eventDateTime,
                event.getTitle(),
                buildMessage(event, false));

        // Marca como agendado
        markEventScheduled(context, eventId);
    }

    /** Constroi um ID único para cada evento */
    private static String buildEventId(ImportantDate event) {
        // Pode usar título + data + hora, ou se houver ID no Firebase, usa isso
        return event.getTitle() + "_" + event.getDate() + "_" + event.getTime();
    }

    /** Verifica se o evento já foi agendado */
    private static boolean isEventScheduled(Context context, String eventId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_EVENT_PREFIX + eventId, false);
    }

    /** Marca o evento como agendado */
    private static void markEventScheduled(Context context, String eventId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_EVENT_PREFIX + eventId, true).apply();
    }

    /** Constroi a mensagem dependendo do tipo de evento */
    private static String buildMessage(ImportantDate event, boolean isBefore) {
        String prefix = isBefore ? "Começa em 30 minutos: " : "Começa agora: ";
        return prefix + event.getTitle();
    }

    /** Agenda a notificação usando WorkManager */
    private static void scheduleNotification(Context context, LocalDateTime dateTime,
                                             String title, String message) {

        long delay = Duration.between(LocalDateTime.now(), dateTime).toMillis();

        if (delay <= 0) return; // não agenda notificações atrasadas

        Data data = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(EventNotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(request);
    }
}
