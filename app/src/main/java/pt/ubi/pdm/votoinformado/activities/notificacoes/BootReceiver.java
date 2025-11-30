package pt.ubi.pdm.votoinformado.activities.notificacoes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BootReceiver extends BroadcastReceiver {
    //classe importante para mesmo com o reiniciar do telemovel o BootReceiver garante que assim que o sistema arranca, vamos à API buscar todas as datas e reagendamos tudo
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Dispositivo reiniciado, reagendando notificações...");

            // Limpar SharedPreferences para garantir que as notificações serão criadas
            context.getSharedPreferences("scheduled_events", Context.MODE_PRIVATE)
                    .edit().clear().apply();

            // Voltar a carregar eventos da API e reagendar notificações
            ApiService apiService = ApiClient.getInstance().getApiService();
            apiService.getDates().enqueue(new Callback<List<ImportantDate>>() {
                @Override
                public void onResponse(Call<List<ImportantDate>> call, Response<List<ImportantDate>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (ImportantDate event : response.body()) {
                            NotificationScheduler.scheduleEventNotifications(context, event);
                        }
                    } else {
                        Log.e("BootReceiver", "Erro ao carregar eventos: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<ImportantDate>> call, Throwable t) {
                    Log.e("BootReceiver", "Erro ao carregar eventos: " + t.getMessage());
                }
            });
        }
    }
}
