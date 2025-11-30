package pt.ubi.pdm.votoinformado.activities.notificacoes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.api.ApiService;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Response;

public class SyncDatesWorker extends Worker {

    public SyncDatesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SyncDatesWorker", "Sincronizando datas importantes em segundo plano...");

        Context context = getApplicationContext();
        ApiService apiService = ApiClient.getInstance().getApiService();

        try {
            // Usando chamada síncrona dentro de um Worker
            Response<List<ImportantDate>> response = apiService.getDates().execute();

            if (response.isSuccessful() && response.body() != null) {
                Log.d("SyncDatesWorker", "Datas recebidas: " + response.body().size());
                for (ImportantDate event : response.body()) {
                    NotificationScheduler.scheduleEventNotifications(context, event);
                }
                return Result.success();
            } else {
                Log.e("SyncDatesWorker", "Erro ao sincronizar datas: " + response.message());
                return Result.failure();
            }
        } catch (IOException e) {
            Log.e("SyncDatesWorker", "Falha na rede ao sincronizar datas: " + e.getMessage());
            return Result.retry(); // Tenta novamente mais tarde em caso de falha de rede
        }
    }
}