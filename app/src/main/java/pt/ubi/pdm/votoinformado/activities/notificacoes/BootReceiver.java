package pt.ubi.pdm.votoinformado.activities.notificacoes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Dispositivo reiniciado, reagendando notificações...");

            // Limpar SharedPreferences para garantir que as notificações serão criadas
            context.getSharedPreferences("scheduled_events", Context.MODE_PRIVATE)
                    .edit().clear().apply();

            // Voltar a carregar eventos do Firebase e reagendar notificações
            DatabaseHelper.getImportantDates(new DatabaseHelper.DataCallback<List<ImportantDate>>() {
                @Override
                public void onCallback(List<ImportantDate> dates) {
                    for (ImportantDate event : dates) {
                        NotificationScheduler.scheduleEventNotifications(context, event);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("BootReceiver", "Erro ao carregar eventos: " + error);
                }
            });
        }
    }

}
