package pt.ubi.pdm.votoinformado.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportantDatesViewModel extends ViewModel {

    private final MutableLiveData<List<ImportantDate>> dates = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Candidato>> candidates = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<ImportantDate>> getDates() {
        return dates;
    }

    public LiveData<Map<String, Candidato>> getCandidates() {
        return candidates;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadData() {
        if (dates.getValue() != null && !dates.getValue().isEmpty()) {
            return; // Data already loaded
        }

        isLoading.setValue(true);
        
        // Fetch Candidates first
        ApiClient.getInstance().getApiService().getCandidates().enqueue(new Callback<List<Candidato>>() {
            @Override
            public void onResponse(Call<List<Candidato>> call, Response<List<Candidato>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Candidato> map = new HashMap<>();
                    for (Candidato c : response.body()) {
                        if (c.getId() != null) map.put(c.getId(), c);
                        if (c.getStringId() != null) map.put(c.getStringId(), c);
                    }
                    candidates.setValue(map);
                    
                    // Then fetch Dates
                    fetchDates();
                } else {
                    isLoading.setValue(false);
                    error.setValue("Erro ao carregar candidatos");
                }
            }

            @Override
            public void onFailure(Call<List<Candidato>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }

    private void fetchDates() {
        ApiClient.getInstance().getApiService().getDates().enqueue(new Callback<List<ImportantDate>>() {
            @Override
            public void onResponse(Call<List<ImportantDate>> call, Response<List<ImportantDate>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ImportantDate> list = new ArrayList<>(response.body());
                    Collections.sort(list, (d1, d2) -> {
                        if (d1.getLocalDate() == null && d2.getLocalDate() == null) return 0;
                        if (d1.getLocalDate() == null) return 1;
                        if (d2.getLocalDate() == null) return -1;
                        return d1.getLocalDate().compareTo(d2.getLocalDate());
                    });
                    dates.setValue(list);
                } else {
                    error.setValue("Erro ao carregar datas");
                }
            }

            @Override
            public void onFailure(Call<List<ImportantDate>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }
}
